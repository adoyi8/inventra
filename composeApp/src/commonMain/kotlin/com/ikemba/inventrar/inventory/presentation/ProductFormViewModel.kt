package com.ikemba.inventrar.inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikemba.inventrar.core.domain.onError
import com.ikemba.inventrar.core.domain.onSuccess
import com.ikemba.inventrar.inventory.data.domain.model.Category
import com.ikemba.inventrar.inventory.data.domain.model.Product
import com.ikemba.inventrar.inventory.data.domain.useCase.CreateProductUseCase
import com.ikemba.inventrar.inventory.data.domain.useCase.DeleteProductUseCase
import com.ikemba.inventrar.inventory.data.domain.useCase.GetCategoriesUseCase
import com.ikemba.inventrar.inventory.data.domain.useCase.GetProductByIdUseCase
import com.ikemba.inventrar.inventory.data.domain.useCase.UpdateProductUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class ProductFormUiState(
    val productId: String? = null, // Key for edit/detail mode
    val name: String = "",
    val unitPrice: String = "",
    val sku: String = "",
    val description: String = "",
    val discount: String = "",
    val vat: String = "0.0",
    val selectedCategoryId: String = "",
    val categories: List<Category> = emptyList(),
    val isInitialLoad: Boolean = true, // New: Tracks if the category list/product data is loaded
    val isLoading: Boolean = false, // Used for saving/deleting
    val isDeleting: Boolean = false,
    val isDeleted: Boolean = false, // New: Flag for successful deletion
    val errorMessage: String? = null,
    val successMessage: String? = null
)


class ProductFormViewModel(
    private val createProductUseCase: CreateProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase, // New
    private val deleteProductUseCase: DeleteProductUseCase, // New
    private val getProductByIdUseCase: GetProductByIdUseCase, // New
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val businessId: String,
    private val staffId: String,
    private val productId: String? = null // New: Optional for Edit/Detail mode
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductFormUiState(productId = productId))
    val uiState = _uiState.asStateFlow()

    private val isEditMode: Boolean get() = productId != null

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Load all categories first
            getCategoriesUseCase(businessId)
                .catch { error ->
                    _uiState.update {
                        it.copy(errorMessage = "Failed to load categories: ${error.message}")
                    }
                }
                .collect { categories ->
                    _uiState.update {
                        it.copy(
                            categories = categories,
                            // If in Add mode, initial load is complete after loading categories
                            isInitialLoad = isEditMode
                        )
                    }

                    // If in Edit mode, load the product data
                    if (isEditMode) {
                        loadProduct()
                    }
                }
        }
    }

    private fun loadProduct() {
        if (productId == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            getProductByIdUseCase(productId, businessId)
                .onSuccess { product ->
                    if (product != null) {
                        _uiState.update {
                            it.copy(
                                name = product.name,
                                unitPrice = product.unitPrice.toString(),
                                sku = product.sku,
                                description = product.description.orEmpty(),
                                discount = product.discount?.toString().orEmpty(),
                                vat = product.vat.toString(),
                                selectedCategoryId = product.categoryId.orEmpty(),
                                isLoading = false,
                                isInitialLoad = false
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isInitialLoad = false,
                                errorMessage = "Product not found."
                            )
                        }
                    }
                }
                .onError { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isInitialLoad = false,
                            errorMessage = "Failed to load product: ${error}"
                        )
                    }
                }
        }
    }

    // --- Update Methods (Same as before) ---
    fun updateName(name: String) { _uiState.update { it.copy(name = name) } }
    fun updateUnitPrice(unitPrice: String) { _uiState.update { it.copy(unitPrice = unitPrice) } }
    fun updateSku(sku: String) { _uiState.update { it.copy(sku = sku) } }
    fun updateDescription(description: String) { _uiState.update { it.copy(description = description) } }
    fun updateDiscount(discount: String) { _uiState.update { it.copy(discount = discount) } }
    fun updateVat(vat: String) { _uiState.update { it.copy(vat = vat) } }
    fun updateSelectedCategory(categoryId: String) { _uiState.update { it.copy(selectedCategoryId = categoryId) } }

    // --- Core Action: Save (Create or Update) ---
    fun saveProduct() {
        val currentState = _uiState.value

        if (currentState.name.isBlank() || currentState.unitPrice.isBlank() || currentState.sku.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill in all required fields") }
            return
        }

        val unitPrice = currentState.unitPrice.toDoubleOrNull()
        if (unitPrice == null || unitPrice <= 0) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid unit price") }
            return
        }

        val discount = currentState.discount.toDoubleOrNull()
        val vat = currentState.vat.toDoubleOrNull() ?: 0.0


        val product = Product(
            id = if(isEditMode)currentState.productId!! else UUID.randomUUID().toString(), // Will be empty for create, set for update
            name = currentState.name,
            unitPrice = unitPrice,
            sku = currentState.sku,
            description = currentState.description,
            discount = discount,
            vat = vat,
            categoryId = currentState.selectedCategoryId.ifBlank { "111" },
            businessId = businessId,
            createdBy = staffId, // Preserve original (or use a helper to load original)
            updatedBy = staffId
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = if (isEditMode) {
                updateProductUseCase(product)
            } else {
                createProductUseCase(product)
            }

            result
                .onSuccess {
                    _uiState.update {
                        // Clear form only on successful CREATE
                        val newName = if (!isEditMode) "" else it.name
                        val newPrice = if (!isEditMode) "" else it.unitPrice
                        // ... and so on for other fields to clear them

                        it.copy(
                            isLoading = false,
                            successMessage = "Product ${if (isEditMode) "updated" else "created"} successfully!",
                            name = newName,
                            unitPrice = newPrice,
                            // ... other fields cleared or left alone based on mode
                        )
                    }
                }
                .onError { error ->

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to ${if (isEditMode) "update" else "create"} product: ${error}"
                        )
                    }
                }
        }
    }

    // --- Core Action: Delete ---
    fun deleteProduct() {
        val id = productId ?: return // Cannot delete if no ID is set

        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, errorMessage = null) }

            deleteProductUseCase(id, businessId)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            isDeleted = true, // Flag for navigation
                            successMessage = "Product deleted successfully!"
                        )
                    }
                }
                .onError { error ->
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            errorMessage = "Failed to delete product: ${error.toString()}"
                        )
                    }
                }
        }
    }

    // --- Cleanup Methods (Same as before) ---
    fun clearError() { _uiState.update { it.copy(errorMessage = null) } }
    fun clearSuccessMessage() { _uiState.update { it.copy(successMessage = null) } }
}