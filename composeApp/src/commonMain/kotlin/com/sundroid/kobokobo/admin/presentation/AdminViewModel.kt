package com.sundroid.kobokobo.admin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.auth.GoogleAuthUiProvider
import com.sundroid.kobokobo.business.presentation.BusinessState
import com.sundroid.kobokobo.core.domain.onError
import com.sundroid.kobokobo.core.domain.onSuccess
import com.sundroid.kobokobo.inventory.data.domain.repository.InventoryRepository
import com.sundroid.kobokobo.user.domain.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminViewModel(
    val inventoryRepository: InventoryRepository
): ViewModel() {

    private val _state = MutableStateFlow(BusinessState())
    private val _inventoryItemState = MutableStateFlow(AddInventoryItemFormState())
    val inventoryItemState = _inventoryItemState.asStateFlow()


    var googleAuthUIProvider : GoogleAuthUiProvider? = null

    val state = _state.asStateFlow()



//    fun addInventoryItem(user: User) {
//        viewModelScope.launch {
//            _state.update {
//                it.copy(isLoading = true)
//            }
//            if(_inventoryItemState.value.sku.isNullOrEmpty()){
//                _state.update {
//                    it.copy(errorMessage = "SKU cannot be empty", isLoading = false)
//                }
//                hideErrorMessageAfterDelay()
//                return@launch
//            }
//            inventoryRepository.createInventory(
//                sku = _inventoryItemState.value.sku,
//                itemName = _inventoryItemState.value.itemName,
//                category = _inventoryItemState.value.category,
//                unitOfMeasure = _inventoryItemState.value.unitOfMeasure,
//                quantity = _inventoryItemState.value.quantity.toDouble(),
//                purchasePricePerUnit = _inventoryItemState.value.purchasePrice.toDouble(),
//                sellingPricePerUnit = _inventoryItemState.value.sellingPrice.toDouble(),
//                reorderLevel = _inventoryItemState.value.reorderLevel.toDouble(),
//                imageUrl = _inventoryItemState.value.imageUrl,
//                organizationId = user.businessId.toString(),
//                createdBy = user.userId.toString()
//            )
//                .onSuccess { createBusinessResponse ->
//
//                    if(createBusinessResponse.responseCode == 0){
//                        _state.update {
//                            it.copy( isLoading = false)
//                        }
//                    }
//                    else{
//                        _state.update {
//                            it.copy(errorMessage = createBusinessResponse?.responseMessage.toString(), isLoading = false)
//                        }
//                        hideErrorMessageAfterDelay()
//                    }
//
//                }
//                .onError { error->
//                    _state.update {
//                        it.copy(errorMessage = error.toString(), isLoading = false)
//                    }
//                    hideErrorMessageAfterDelay()
//                }
//        }
//    }



    fun updateSku(string: String) {
        _inventoryItemState.update {
            it.copy(sku = string)
        }
    }

    fun updateItemName(string: String) {
        _inventoryItemState.update {
            it.copy(itemName = string)
        }
    }

    fun updateCategory(string: String) {
        _inventoryItemState.update {
            it.copy(category = string)
        }
    }

    fun updateUnitOfMeasure(string: String) {
        _inventoryItemState.update {
            it.copy(unitOfMeasure = string)
        }
    }

    fun updateQuantity(i: Int) {
        _inventoryItemState.update {
            it.copy(quantity = i.toString())
        }
    }
    fun updatePurchasePrice(i: Double){
        _inventoryItemState.update {
            it.copy(purchasePrice = i.toString())
        }
    }
    fun updateReorderLevel(string: String) {
        _inventoryItemState.update {
            it.copy(reorderLevel = string)
        }
    }

    fun updateSellingPrice(d: Double) {
        _inventoryItemState.update {
            it.copy(sellingPrice = d.toString())
        }
    }
    fun hideErrorMessageAfterDelay(){
        viewModelScope.launch {
            delay(4000)
            _state.update {
                it.copy(errorMessage = "")
            }
        }
    }
}
