package com.ikemba.inventrar.dashboard.presentation

import androidx.compose.animation.core.MutableTransitionState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikemba.inventrar.app.Route
import com.ikemba.inventrar.cart.data.mappers.toPostSalesModel
import com.ikemba.inventrar.cart.data.mappers.toPostSalesRequest
import com.ikemba.inventrar.cart.data.mappers.toPostSalesRequestEntity
import com.ikemba.inventrar.cart.data.mappers.toReceipt
import com.ikemba.inventrar.cart.domain.Cart
import com.ikemba.inventrar.cart.domain.CartItem
import com.ikemba.inventrar.cart.domain.CartRepository
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.onError
import com.ikemba.inventrar.core.domain.onSuccess
import com.ikemba.inventrar.core.presentation.NavigationViewModel
import com.ikemba.inventrar.dashboard.data.mappers.toCategory
import com.ikemba.inventrar.dashboard.data.mappers.toCategoryEntity
import com.ikemba.inventrar.dashboard.data.mappers.toItem
import com.ikemba.inventrar.dashboard.data.mappers.toItemEntity
import com.ikemba.inventrar.dashboard.domain.Category
import com.ikemba.inventrar.dashboard.domain.Item
import com.ikemba.inventrar.dashboard.domain.ProductRepository
import com.ikemba.inventrar.dashboard.utils.Util
import com.ikemba.inventrar.heldOrder.data.dto.SingleHeldOrderDto
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest
import com.ikemba.inventrar.user.domain.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.apache.commons.text.StringEscapeUtils

class DashboardViewModel(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    private var observeUsers: Job? = null
    private var observeCategory: Job? = null
    private var observeVisibleItem: Job? = null
    private var observeAllItemsJob: Job? = null
    private var observeSavedSalesJob: Job? = null


    val state = _state.asStateFlow()
        .onStart {
            observeUsers()
            observeCategory()
            observeAllItems()
            observeItems()
            observeSavedItems()
             postSavedSales()
            getProducts()

        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

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
                        visibleItems = items.map { it.toItem() }
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
                        allItems = items.map { it.toItem() }
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
                    Util.accessToken = users.last().accessToken!!
                    Util.CURRENCY_CODE = if(users.last().currencyEntity==null) "₦" else  StringEscapeUtils.unescapeHtml4(users.last().currencyEntity!!)
                    //getProducts()
                }

            }
            .launchIn(viewModelScope)
    }


    fun getProducts() {
        viewModelScope.launch {
            productRepository.getProducts(Util.accessToken)
                .onSuccess { productResponse ->
                    if (productResponse.responseCode == 0) {
                        productResponse.product!!.categoryDto!!.forEach {
                            productRepository.saveCategory(it.toCategory().toCategoryEntity())
                            productResponse.product?.itemDto?.forEach {
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
            NavigationViewModel.navController?.navigate(Route.Login)

        }
    }

    fun hideShowConfirmLogout() {
        _state.update {
            it.copy(showConfirmLogout = false)
        }
    }

    fun hideChangePasswordDialog() {
        viewModelScope.launch {
            _state.update {
                it.copy(showChangePasswordDialog = false)
            }
        }

    }

    fun showShowConfirmLogout() {
        viewModelScope.launch {
            _state.update {
                it.copy(showConfirmLogout = true)
            }
        }
    }
    fun showChangePasswordDialog() {
        viewModelScope.launch {
            _state.update {
                it.copy(showChangePasswordDialog = true)
            }
        }
    }

    fun onMenuSelected(index: Int) {
        _state.update {
            it.copy(selectedMenuIndex = MutableTransitionState(index))
        }
  //      state.value.selectedMenuIndex.targetState = index
    }

    fun addItemToCart(item: Item) {
        if(state.value.cartItems.isEmpty() && state.value.reference.isNullOrEmpty()){
            _state.update {
                it.copy(reference = Util.generateTransactionReference())
            }
        }
        val currentCartItem = state.value.cartItems.filter { it.itemId == item.id }
        if (currentCartItem.isEmpty()) {
            val cartItem = CartItem(
                itemId = item.id,
                itemName = item.name,
                quantity = 1,
                price = item.unitPrice,
                vatRate = item.vat,
                discount = item.discount,
                image = item.image
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
            val newItems = state.value.allItems.filter { it.categoryId == categoryId }
            _state.update { it ->
                it.copy(
                    visibleItems = newItems
                )
            }
        }else{
            val newItems = state.value.allItems
            _state.update { it ->
                it.copy(
                    visibleItems = newItems
                )
            }
        }

    }

    fun searchItems(query: String) {
        val newItems = state.value.allItems.filter { it.name.contains(query, ignoreCase = true) }
        _state.update { it ->
            it.copy(
                visibleItems = newItems
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

    fun postSale() {
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(showPostSalesDialog = MutableTransitionState(true))
                }
                val cart = Cart(items = state.value.cartItems)
                cart.paymentMethod = state.value.paymentMethod
                if(!state.value.reference.isNullOrEmpty()){
                    cart.reference = state.value.reference
                }
                else{
                    cart.reference = Util.generateTransactionReference()
                }

                cart.paymentMethod = state.value.paymentMethod;
                val request = cart.toPostSalesRequest()
                request.is_held = false

                cartRepository.postCartSales(
                    state.value.users!!.first()!!.accessToken!!,
                    request
                ).onSuccess { response ->


                    if (response!!.responseCode == 0) {
                        cartRepository.getOrderReceipt(
                            state.value.users!!.first()!!.accessToken!!,
                            VoidOrderRequest(cart.reference)
                        ).onSuccess { receiptResponseDto ->
                            hideAllDialogs()
                            if (receiptResponseDto!!.responseCode == 0) {
                                val receipt = receiptResponseDto.toReceipt()
                                _state.update {
                                    it.copy(
                                        showPostSalesDialog = MutableTransitionState(false),
                                        receiptModel = receipt,
                                        showReceipt = MutableTransitionState(true), cartItems = mutableListOf(), reference = ""
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
                            it.copy(cartItems = mutableListOf(), reference = "")
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
                _state.update {
                    it.copy(showPostSalesDialog = MutableTransitionState(true))
                }
                val cart = Cart(items = state.value.cartItems)
                cart.paymentMethod = state.value.paymentMethod
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

    fun proceedOrders(heldOrdersResponse: SingleHeldOrderDto, reference: String) {

        val cart = Cart(mutableListOf())
        heldOrdersResponse.data.forEach {
            val cartItem = CartItem(
                itemId = it.productId,
                itemName = it.productName,
                quantity = it.qty.toInt(),
                vatRate = it.vatRate.toDouble()
            )
            cartItem.price = it.subTotal.toDouble() / it.qty.toInt()
            cart.items.add(cartItem)
        }
        cart.reference = reference
        _state.update {
            it.copy(
                cartItems = cart.items,
                selectedMenuIndex = MutableTransitionState(0)
            )
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
                        state.value.users!!.first()!!.accessToken!!,
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
        println("starting 11")
        if(!qrCodeText.startsWith("xsxy", ignoreCase = true)){
            println("starting 10")

            showSnackBar("Wrong Input")
            return
        }
        println("starting 9")
        val productId = qrCodeText.subSequence(4,qrCodeText.length)
        println("starting 6 "+ productId)
        println("starting 5 "+ state.value.allItems.size)

        state.value.allItems.forEach {
            println("show " + it.id)
        }
        val matchedProducts = state.value.allItems.filter { it.id.toString().equals( productId.toString()) }
        if(matchedProducts.isEmpty()){
            println("starting 8")
            showSnackBar("Item not found")
            return
        }
        println("starting 7")
        val matchedItem = matchedProducts.first()
        addItemToCart(matchedItem)
    }
    fun showSnackBar(message: String){
        viewModelScope.launch {
            state.value.snackBarHostState.showSnackbar(message)
        }
    }
    fun setPaymentMethod(paymentMethod: String){
        _state.update {
            it.copy(paymentMethod = paymentMethod)
        }
    }
}