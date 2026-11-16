package com.sundroid.kobokobo.dashboard.presentation

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.State
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.annotation.ExperimentalCoilApi
import coil3.network.ConnectivityChecker
import com.google.firebase.auth.FirebaseAuth
import com.sundroid.kobokobo.app.Route
import com.sundroid.kobokobo.business.data.domain.BusinessRepository
import com.sundroid.kobokobo.cart.data.database.HeldCartEntity
import com.sundroid.kobokobo.cart.data.mappers.toPostSalesModel
import com.sundroid.kobokobo.cart.data.mappers.toPostSalesRequest
import com.sundroid.kobokobo.cart.data.mappers.toPostSalesRequestEntity
import com.sundroid.kobokobo.cart.data.mappers.toReceipt
import com.sundroid.kobokobo.cart.domain.Cart
import com.sundroid.kobokobo.cart.domain.CartItem
import com.sundroid.kobokobo.cart.domain.CartRepository
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.onError
import com.sundroid.kobokobo.core.domain.onSuccess
import com.sundroid.kobokobo.core.presentation.NavigationViewModel
import com.sundroid.kobokobo.dashboard.data.mappers.toCategory
import com.sundroid.kobokobo.dashboard.data.mappers.toCategoryEntity
import com.sundroid.kobokobo.dashboard.data.mappers.toItem
import com.sundroid.kobokobo.dashboard.data.mappers.toItemEntity
import com.sundroid.kobokobo.dashboard.domain.Category
import com.sundroid.kobokobo.dashboard.domain.ProductRepository
import com.sundroid.kobokobo.dashboard.utils.Util
import com.sundroid.kobokobo.heldOrder.data.dto.VoidOrderRequest
import com.sundroid.kobokobo.inventory.data.domain.model.Product
import com.sundroid.kobokobo.user.domain.UserRepository
import com.sundroid.kobokobo.utils.getConnectivityChecker
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import my.connectivity.kmp.data.model.NetworkStatus
import org.apache.commons.text.StringEscapeUtils

class DashboardViewModel(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val businessRepository: BusinessRepository


) : ViewModel()
{
    private val _state = MutableStateFlow(DashboardState())
    private var observeUsers: Job? = null
    private var observeCategory: Job? = null
    private var observeVisibleItem: Job? = null
    private var observeAllItemsJob: Job? = null
    private var observeSavedSalesJob: Job? = null
    // A dashboard can be restored from the local session before Firebase has finished
    // restoring its auth session. Do not make ViewModel construction depend on that
    // asynchronous state.
    private val userId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid
    @OptIn(ExperimentalCoilApi::class)
    private val connectivityChecker: ConnectivityChecker = getConnectivityChecker()
    private var observeHeldCartsJob: Job? = null
    private var businessId: String = ""


    init {
        // Move all observation starters here
        observeUsers()
        observeCategory()
        observeAllItems()
        observeItems()
        observeSavedItems()
        postSavedSales()
        observeHeldCarts()
        getProducts() // This was already here
    }

    // The stateFlow becomes much simpler
    val state = _state.asStateFlow()
        // .onStart { ... }  <-- REMOVE THIS BLOCK
        .stateIn(
            viewModelScope,
            // Using Eagerly ensure the state is always kept up-to-date
            // even when the UI is not observing (e.g. in background),
            // which is often what you want for a Dashboard.
            // If you prefer WhileSubscribed, keep it, but the init block is key.
            SharingStarted.Eagerly,
            _state.value
        )
    private fun observeHeldCarts() {
        observeHeldCartsJob?.cancel()
        observeHeldCartsJob = cartRepository.getAllHeldCarts()
            .onEach { heldCarts ->
                _state.update { it.copy(heldCarts = heldCarts) }
            }
            .launchIn(viewModelScope)
    }

    fun holdCurrentCart() {
        val currentState = state.value
        if (currentState.cartItems.isEmpty()) {
            showSnackBar("Cart is empty")
            return
        }

        viewModelScope.launch {
            val reference = if (currentState.reference.isNotEmpty()) currentState.reference else Util.generateTransactionReference()
            // Calculate total for the entity
            val totalAmount = currentState.cartItems.sumOf { it.getSubTotal() }

            val heldCart = HeldCartEntity(
                reference = reference,
                items = currentState.cartItems.toList(), // Create a copy
                totalAmount = totalAmount
            )

            cartRepository.saveHeldCart(heldCart)
                .onSuccess {
                    // Clear current cart and reference on success
                    _state.update { it.copy(cartItems = mutableListOf(), reference = "") }
                    showSnackBar("Cart held successfully")
                }
                .onError { error ->
                    showSnackBar("Failed to hold cart: ")
                }
        }
    }

    fun resumeHeldCart(heldCart: HeldCartEntity) {
        viewModelScope.launch {
            // 1. Restore items and reference to the active cart
            _state.update {
                it.copy(
                    cartItems = heldCart.items.toMutableList(),
                    reference = heldCart.reference,
                    selectedMenuIndex = MutableTransitionState(0) // Navigate back to POS
                )
            }
            // 2. Delete the held cart from the database
            cartRepository.deleteHeldCart(heldCart)
                .onError { error ->
                    // Handle deletion error (maybe log it), but cart is resumed in UI
                    println("Error deleting held cart after resume:")
                }
        }
    }

    fun deleteHeldCart(heldCart: HeldCartEntity) {
        viewModelScope.launch {
            cartRepository.deleteHeldCart(heldCart)
                .onSuccess {
                    showSnackBar("Held cart deleted")
                }
                .onError { error ->
                    showSnackBar("Failed to delete held cart: ")
                }
        }
    }

    private fun observeCategory() {
        observeCategory?.cancel()
        observeCategory = productRepository
            .getAllCategory()
            .onEach { categories ->
                if (categories.isNotEmpty()) {
                    val allCategory: Category = Category("All", "All", "All")
                    val allCategories: MutableList<Category> = mutableListOf()
                    allCategories.add(allCategory)
                    allCategories.addAll(categories.map { it.toCategory() }.toMutableList())
                    _state.update { it ->
                        it.copy(
                            categories = allCategories
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeSavedItems() {
        observeSavedSalesJob?.cancel()
        observeSavedSalesJob = cartRepository
            .getAllSales()
            .onEach { savedSales ->
                _state.update { it ->
                    it.copy(
                        savedSales = savedSales.map { it.toPostSalesModel() }
                    )
                }
            }
            .launchIn(viewModelScope)
    }


    private fun observeItems() {
        observeVisibleItem?.cancel()
        observeVisibleItem = productRepository
            .getAllItems()
            .onEach { items ->
                _state.update { it ->
                    it.copy(
                        visibleProducts = items.map { it.toItem() }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeAllItems() {
        observeAllItemsJob?.cancel()
        observeAllItemsJob = productRepository
            .getAllItems()
            .onEach { items ->
                _state.update { it ->
                    it.copy(
                        allProducts = items.map { it.toItem() }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeUsers() {
        observeUsers?.cancel()
        observeUsers = userRepository
            .getAllUsers()
            .onEach { users ->
                _state.update {
                    it.copy(
                        users = users
                    )
                }
                if(users.isNotEmpty()) {
                    Util.CURRENCY_CODE = if(users.last().currencyEntity==null) "₦" else  StringEscapeUtils.unescapeHtml4(users.last().currencyEntity!!)
                    //getProducts()
                }

            }
            .launchIn(viewModelScope)
    }


    fun getProducts() {
        viewModelScope.launch {
            getPaymentMethods()
            productRepository.getProducts(businessId)
                .onSuccess { productResponse ->
                    if (productResponse.responseCode == 0) {
                        productRepository.clear()
                        productResponse.product!!.categoryDto!!.forEach {

                            productRepository.saveCategory(it.toCategory().toCategoryEntity())
                            productResponse.product?.itemDto?.forEach {
                                println("John wick 2 "+ it.costPrice)
                                productRepository.saveItem(it.toItem().toItemEntity())
                            }
                        }
                    } else {
                        if (productResponse.responseMessage!!.contains(
                                Util.EXPIRED_TOKEN_MESSAGE,
                                true
                            )
                        ) {
                            NavigationViewModel.navController!!.navigate(Route.Login)
                        }
                    }
                }
                .onError { error ->
                   if(error.equals(DataError.Remote.EXPIRED_TOKEN)){
                       NavigationViewModel.navController!!.navigate(Route.Login)
                   }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            hideShowConfirmLogout()
            userRepository.deleteAllUsers()
            onMenuSelected(0)
            NavigationViewModel.navController?.navigate(Route.Login)

        }
    }

    fun hideShowConfirmLogout() {
        _state.update {
            it.copy(showConfirmLogout = false)
        }
    }


    fun showShowConfirmLogout() {
        viewModelScope.launch {
            _state.update {
                it.copy(showConfirmLogout = true)
            }
        }
    }

    fun onMenuSelected(index: Int) {
        _state.update {
            it.copy(selectedMenuIndex = MutableTransitionState(index))
        }
    }

    fun addItemToCart(product: Product) {
        if(state.value.cartItems.isEmpty() && state.value.reference.isNullOrEmpty()){
            _state.update {
                it.copy(reference = Util.generateTransactionReference())
            }
        }
        val currentCartItem = state.value.cartItems.filter { it.itemId == product.id }
        println("Bukky "+ product.costPrice)
        if (currentCartItem.isEmpty()) {
            val cartItem = CartItem(
                itemId = product.id,
                itemName = product.name,
                quantity = 1,
                price = product.unitPrice,
                costPrice = product.costPrice,
                vatRate = product.vat,
                discount = product.discount,
              //  image = product.image
            )
            val currentItems: MutableList<CartItem> = mutableListOf()
            state.value.cartItems.forEach { item ->
                currentItems.add(item)
            }
            currentItems.add(cartItem)
            _state.update { it1 ->
                it1.copy(
                    cartItems = currentItems
                )
            }
        } else {

            val itemIndex = state.value.cartItems.indexOf(currentCartItem.first())
            val item = currentCartItem.first()
            val newCartItem = CartItem(
                item.itemId,
                item.itemName,
                item.quantity + 1,
                price = item.price,
                costPrice = item.costPrice,
                vatRate = item.vatRate,
                discount = item.discount,
                image = item.image
            )
            val currentItems: MutableList<CartItem> = mutableListOf()
            state.value.cartItems.filter { it.itemId != item.itemId }.forEach { item1 ->
                currentItems.add(item1)
            }

            _state.update { it1 ->

                it1.copy(
                    cartItems = mutableListOf(),
                )
            }
            currentItems.add(itemIndex, newCartItem)
            _state.update { it1 ->
                it1.copy(
                    cartItems = currentItems
                )
            }
        }
    }

    fun addItemToCart(item: CartItem) {

        val itemIndex = state.value.cartItems.indexOf(item)
        val newCartItem = CartItem(
            item.itemId,
            item.itemName,
            item.quantity + 1,
            price = item.price,
            costPrice = item.costPrice,
            vatRate = item.vatRate,
            discount = item.discount,
            image = item.image
        )
        val currentItems: MutableList<CartItem> = mutableListOf()
        state.value.cartItems.filter { it.itemId != item.itemId }.forEach { item1 ->
            currentItems.add(item1)
        }

        _state.update { it1 ->

            it1.copy(
                cartItems = mutableListOf(),
            )
        }
        currentItems.add(itemIndex, newCartItem)
        _state.update { it1 ->
            it1.copy(
                cartItems = currentItems
            )
        }

    }


    fun subtractItemFromCart(item: CartItem) {
        val currentCartItem = state.value.cartItems.filter { it.itemId == item.itemId }
        if (currentCartItem.isEmpty()) {

        } else {

            val itemIndex = state.value.cartItems.indexOf(currentCartItem.first())
            val item = currentCartItem.first()
            val newCartItem = CartItem(
                item.itemId,
                item.itemName,
                item.quantity,
                item.price,
                costPrice = item.costPrice,
                vatRate = item.vatRate,
                discount = item.discount,
                image = item.image
            )
            if (newCartItem.quantity > 0) {
                newCartItem.quantity -= 1
            } else {
                newCartItem.quantity = 0
            }
            val currentItems: MutableList<CartItem> = mutableListOf()
            state.value.cartItems.filter { it.itemId != item.itemId }.forEach { item1 ->
                currentItems.add(item1)
            }

            _state.update { it1 ->

                it1.copy(
                    cartItems = mutableListOf(),
                )
            }
            if (newCartItem.quantity > 0) {
                currentItems.add(itemIndex, newCartItem)
            }
            _state.update { it1 ->
                it1.copy(
                    cartItems = currentItems
                )
            }
        }
    }

    fun removeItemFromCart(item: CartItem) {

        val currentItems: MutableList<CartItem> = mutableListOf()
        state.value.cartItems.filter { it.itemId != item.itemId }.forEach { item1 ->
            currentItems.add(item1)
        }

        _state.update { it1 ->

            it1.copy(
                cartItems = mutableListOf(),
            )
        }
        _state.update { it1 ->
            it1.copy(
                cartItems = currentItems
            )
        }

    }

    fun selectCategory(categoryId: String) {
        if(categoryId != "All") {
            val newItems = state.value.allProducts.filter { it.categoryId == categoryId }
            _state.update { it ->
                it.copy(
                    visibleProducts = newItems
                )
            }
        }else{
            val newItems = state.value.allProducts
            _state.update { it ->
                it.copy(
                    visibleProducts = newItems
                )
            }
        }

    }

    fun searchItems(query: String) {
        val newItems = state.value.allProducts.filter { it.name.contains(query, ignoreCase = true) }
        _state.update { it ->
            it.copy(
                visibleProducts = newItems
            )
        }


    }

    fun toggleShowConfirmTransaction(b: Boolean) {
        _state.update { it ->
            it.copy(showConfirmTransaction = MutableTransitionState(b))
        }
    }

    fun toggleShowPaymentMethodNotAvailable(b: Boolean) {
        _state.update { it ->
            it.copy(showPaymentMethodNotAvailableDialog = MutableTransitionState(b))
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    fun postSale(networkStatus: State<NetworkStatus>) {
        viewModelScope.launch {
            println("kdh 1")
            try {
                val currentUserId = userId ?: run {
                    showSnackBar("Your session is no longer active. Please sign in again.")
                    return@launch
                }
                println("kdh 2 "+ currentUserId)

                state.value.cartItems.forEach {
                    println("Bambi "+ it.costPrice)
                }

                val cart = Cart(items = state.value.cartItems, businessId = businessId, userId = currentUserId, userName = state.value.users!!.first()!!.getFullName())
                println("kdh 3")
                cart.paymentMethod = state.value.paymentMethod
                cart.paymentMethodText = state.value.paymentMethodText
                cart.isPaid = !state.value.proceedWithoutPayment && state.value.paymentMethod.isNotBlank()
                cart.paymentProcessedBy = if (cart.isPaid) cart.userName else null
                println("kdh 4")
                if(!state.value.reference.isNullOrEmpty()){
                    cart.reference = state.value.reference
                    println("kdh 5")

                }
                else{
                    cart.reference = Util.generateTransactionReference()
                    println("kdh 6")

                }

                println("kdh 7")
                cart.paymentMethod = state.value.paymentMethod;
                cart.paymentMethodText = state.value.paymentMethodText;
                println("kdh 8")
                val request = cart.toPostSalesRequest()
                println("kdh 9")
                request.is_held = false
                println("kdh 10")
                println("biden status "+ networkStatus.value)
              if(networkStatus.value == (NetworkStatus.Available)){
                  println("biden yes")
                  _state.update {
                      it.copy(
                          showPostSalesDialog = MutableTransitionState(true),
                          showCartSheet = false
                      )
                  }
              }
                else{
                  println("biden no")
                  _state.update {
                      it.copy(
                          showPostSalesDialog = MutableTransitionState(false),
                          cartItems = mutableListOf(), reference = "", proceedWithoutPayment = false,
                          showCartSheet = false
                      )
                  }
                    showSnackBar("Sales Saved, will be posted later")

              }

                cartRepository.postCartSales(
                    request
                ).onSuccess { response ->


                    println("kdh 11 "+ response)
                    if (response!!.responseCode == 0) {
                        cartRepository.getOrderReceipt(
                            VoidOrderRequest(cart.reference, businessId = businessId)
                        ).onSuccess { receiptResponseDto ->
                            println("mimi " + receiptResponseDto?.data?.items?.size)
                            hideAllDialogs()
                            if (receiptResponseDto!!.responseCode == 0) {
                                val receipt = receiptResponseDto.toReceipt()
                                println("obi " + receipt.reference)

                                _state.update {
                                    it.copy(
                                        showPostSalesDialog = MutableTransitionState(false),
                                        receiptModel = receipt,
                                        showReceipt = MutableTransitionState(true),
                                        cartItems = mutableListOf(),
                                        reference = "",
                                        proceedWithoutPayment = false
                                    )
                                }
                            } else {
                                _state.update {
                                    it.copy(
                                        showPostSalesDialog = MutableTransitionState(false),
                                    )
                                }
                                showSnackBar(receiptResponseDto.responseMessage.toString())
                            }
                        }

                    } else if (response!!.responseCode == 20) {
                        _state.update {
                            it.copy(
                                showPostSalesDialog = MutableTransitionState(false),
                            )
                        }
                        showSnackBar(response.responseMessage.toString())

                    } else {
                        _state.update {
                            it.copy(
                                showPostSalesDialog = MutableTransitionState(false),
                            )
                        }
                        showSnackBar(response.responseMessage.toString())
                    }

                }.onError { response ->

                    hideAllDialogs()
                    if (response.equals(DataError.Remote.EXPIRED_TOKEN)) {
                        NavigationViewModel.navController!!.navigate(Route.Login)
                    }
                    else{
                        cartRepository.saveSales(cart.toPostSalesRequest().toPostSalesRequestEntity())
                        _state.update {
                            it.copy(cartItems = mutableListOf(), reference = "", proceedWithoutPayment = false)
                        }
                        if(!state.value.disableShowSavingSalesDialog) {
                            _state.update {
                                it.copy(showSavingSalesDialog = MutableTransitionState(true))
                            }
                        }
                    }

                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        showPostSalesDialog = MutableTransitionState(false),
                    )
                }
                showSnackBar(e.message.toString())
            }
        }
    }




    fun holdOrder() {
        viewModelScope.launch {
            try {
                val currentUserId = userId ?: run {
                    showSnackBar("Your session is no longer active. Please sign in again.")
                    return@launch
                }
                _state.update {
                    it.copy(showPostSalesDialog = MutableTransitionState(true))
                }
                val cart = Cart(items = state.value.cartItems, businessId = businessId, userId = currentUserId, userName = state.value.users!!.first()!!.getFullName())
                cart.paymentMethod = "cash"
                cart.paymentMethodText = "Cash"
                cart.reference = Util.generateTransactionReference()
                val request = cart.toPostSalesRequest()
                request.is_held = true;
                cartRepository.holdOrder(
                    state.value.users!!.first()!!.accessToken!!,
                    request
                ).onSuccess { response ->


                    if (response!!.responseCode == 0) {
                        _state.update {
                            it.copy(
                                showPostSalesDialog = MutableTransitionState(false), cartItems = mutableListOf(), reference = ""
                            )
                        }
                    } else if (response!!.responseCode == 20) {
                        _state.update {
                            it.copy(
                                showPostSalesDialog = MutableTransitionState(false),
                            )
                        }
                        showSnackBar(response.responseMessage.toString())
                        if (response.responseMessage!!.contains(Util.EXPIRED_TOKEN_MESSAGE, true)) {
                            NavigationViewModel.navController!!.navigate(Route.Login)
                        }
                    } else {
                        _state.update {
                            it.copy(
                                showPostSalesDialog = MutableTransitionState(false),
                            )
                        }
                        showSnackBar(response.responseMessage.toString())
                    }

                }.onError { response ->

                    if (response.equals(DataError.Remote.EXPIRED_TOKEN)) {
                        NavigationViewModel.navController!!.navigate(Route.Login)

                    }
                    _state.update {
                        it.copy(
                            showPostSalesDialog = MutableTransitionState(false),
                        )
                    }
                    showSnackBar(response.toString())
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        showPostSalesDialog = MutableTransitionState(false),
                    )
                }
                showSnackBar(e.message.toString())
            }

        }
    }


    fun toggleShowReceipt(b: Boolean) {
        _state.update { it ->
            it.copy(showReceipt = MutableTransitionState(b))
        }
    }



    fun postSavedSales() {
        viewModelScope.launch {
            while (true) {
                delay(30000)
                state.value.savedSales.forEach {

                    val request = it.toPostSalesRequest()
                    request.is_held = false
                    cartRepository.postCartSales(
                        request
                    ).onSuccess { response ->
                        if (response?.responseCode == 0) {
                            cartRepository.deleteSales(it.reference)
                        }
                        else if(response?.responseMessage!!.contains("Duplicate", true)){
                            cartRepository.deleteSales(it.reference)
                        }

                    }.onError { error->

                    }
                }
            }
        }

    }
    fun toggleDisableShowSavingSalesDialog(value: Boolean){
        _state.update {
            it.copy(disableShowSavingSalesDialog = value)
        }
    }
    fun toggleShowSavingSalesDialog(value: Boolean){
        _state.update {
            it.copy(showSavingSalesDialog = MutableTransitionState(value))
        }
    }
    fun hideAllDialogs(){
        _state.update {
            it.copy(showSavingSalesDialog = MutableTransitionState(false), showPostSalesDialog = MutableTransitionState(false),
                showReceipt = MutableTransitionState(false), showConfirmLogout = false, showConfirmTransaction = MutableTransitionState(false), showPaymentMethodNotAvailableDialog = MutableTransitionState(false)
            )
        }
    }

    fun clearCart() {
        _state.update {
            it.copy(cartItems = mutableListOf() )
        }
    }

    fun addToCartFromScanner(qrCodeText: String){

        val productId = qrCodeText


        val matchedProducts = state.value.allProducts.filter { it.sku == productId.toString() }
        if(matchedProducts.isEmpty()){
            showSnackBar("Item not found")
            return
        }
        val matchedItem = matchedProducts.first()
        addItemToCart(matchedItem)
    }
    fun showSnackBar(message: String){
        viewModelScope.launch {
            state.value.snackBarHostState.showSnackbar(message)
        }
    }
    fun setPaymentMethod(paymentMethod: String){
        // Find the method object to get the name
        val methodObj = state.value.paymentMethods.find { it.id == paymentMethod } 
            ?: state.value.paymentMethods.find { it.name.equals(paymentMethod, ignoreCase = true) }
        
        val methodText = methodObj?.name ?: paymentMethod // Fallback to provided string if not found (e.g. legacy name)

        println("shah 1 "+ methodText)
        _state.update {
            it.copy(
                paymentMethod = paymentMethod,
                paymentMethodText = methodText, // Update state with the text
                proceedWithoutPayment = false
            )
        }
    }

    fun setProceedWithoutPayment(proceedWithoutPayment: Boolean) {
        _state.update {
            it.copy(
                proceedWithoutPayment = proceedWithoutPayment,
                paymentMethod = if (proceedWithoutPayment) "" else it.paymentMethod,
                paymentMethodText = if (proceedWithoutPayment) "" else it.paymentMethodText
            )
        }
    }

    fun showCartSheet(b: Boolean) {
        _state.update {
            it.copy(showCartSheet = b)
        }
    }

    fun setBusinessId(lbusinessId: String?) {
        lbusinessId?.let {
            businessId = lbusinessId
        }
    }

    fun getCategories() {
        observeCategory() // Re-triggers manual fetch/observe logic if needed, or simply calls repository
        // Since observeCategory uses flow observation, it might not be a 'fetch once'. 
        // If the goal is to optimization, check if empty. 
        // But observeCategory sets up a flow. 
        // Let's make a public method that acts as a manual trigger if needed, or just relying on init.
        // The user wanted to call viewModel.getCategories().
        // Let's implement it to basically restart the observation or fetch.
        // Actually, observeCategory is already called in Init.
        // If we want a manual fetch:
        observeCategory()
    }

    fun getPaymentMethods() {
        if(businessId.isEmpty()) return
        viewModelScope.launch {
            businessRepository.getPaymentMethods(businessId)
                .onSuccess { methods ->
                    _state.update { 
                        it.copy(
                            paymentMethods = methods,
                            paymentMethod = it.paymentMethod,
                            paymentMethodText = it.paymentMethodText
                        ) 
                    }
                }
        }
    }
}
