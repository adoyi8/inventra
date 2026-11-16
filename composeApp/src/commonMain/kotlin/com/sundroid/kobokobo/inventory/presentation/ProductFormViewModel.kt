package com.sundroid.kobokobo.inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.core.domain.onError
import com.sundroid.kobokobo.core.domain.onSuccess
import com.sundroid.kobokobo.inventory.data.domain.model.Category
import com.sundroid.kobokobo.inventory.data.domain.model.Product
import com.sundroid.kobokobo.inventory.data.domain.useCase.CreateProductUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.DeleteProductUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.GetCategoriesUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.GetProductByIdUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.UpdateProductUseCase
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
    val costPrice: String = "",
    val sku: String = "",
    val description: String = "",
    val discount: String = "",
    val vat: String = "0.0",
    val sortOrder: String = "0",
    val selectedCategoryId: String = "",
    val categories: List<Category> = emptyList(),
    val stock: String = "", // For initial stock input or display
    val currentStock: Double = 0.0, // To hold the actual current stock in edit mode
    val isAddStockDialogOpen: Boolean = false, // State for the Add Stock dialog
    val stockToAdd: String = "", // Input for the Add Stock dialog
    val isInitialLoad: Boolean = true, // New: Tracks if the category list/product data is loaded
    val isLoading: Boolean = false, // Used for saving/deleting
    val isDeleting: Boolean = false,
    val isDeleted: Boolean = false, // Flag for successful deletion
    val isSaved: Boolean = false, // Flag for successful save
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
) : ViewModel()
{

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
                                costPrice = product.costPrice.toString(),
                                sku = product.sku,
                                description = product.description.orEmpty(),
                                discount = product.discount?.toString().orEmpty(),
                                vat = product.vat.toString(),
                                sortOrder = product.sortOrder.toString(),
                                selectedCategoryId = product.categoryId.orEmpty(),
                                currentStock = product.stock ?: 0.0,
                                stock = (product.stock ?: 0.0).toString(), // Pre-fill stock for display
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

    fun updateCostPrice(costPrice: String) { _uiState.update { it.copy(costPrice = costPrice) } } // <--- New update method
    fun updateUnitPrice(unitPrice: String) { _uiState.update { it.copy(unitPrice = unitPrice) } }
    fun updateSku(sku: String) { _uiState.update { it.copy(sku = sku) } }
    fun updateDescription(description: String) { _uiState.update { it.copy(description = description) } }
    fun updateDiscount(discount: String) { _uiState.update { it.copy(discount = discount) } }
    fun updateVat(vat: String) { _uiState.update { it.copy(vat = vat) } }
    fun updateSortOrder(sortOrder: String) { _uiState.update { it.copy(sortOrder = sortOrder) } }
    fun updateSelectedCategory(categoryId: String) { _uiState.update { it.copy(selectedCategoryId = categoryId) } }
    fun updateStock(stock: String) { _uiState.update { it.copy(stock = stock) } }

    // --- Add Stock Dialog Logic ---
    fun openAddStockDialog() { _uiState.update { it.copy(isAddStockDialogOpen = true, stockToAdd = "") } }
    fun closeAddStockDialog() { _uiState.update { it.copy(isAddStockDialogOpen = false) } }
    fun updateStockToAdd(amount: String) { _uiState.update { it.copy(stockToAdd = amount) } }

    fun addStock() {
        val currentState = _uiState.value
        val amountToAdd = currentState.stockToAdd.toDoubleOrNull()

        if (amountToAdd == null || amountToAdd <= 0) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid stock amount to add") }
            return
        }

        val newStock = currentState.currentStock + amountToAdd
        val product = Product(
            id = productId!!, // Must be in edit mode
            name = currentState.name,
            unitPrice = currentState.unitPrice.toDoubleOrNull() ?: 0.0,
            costPrice = currentState.costPrice.toDoubleOrNull() ?: 0.0,
            sku = currentState.sku,
            description = currentState.description,
            discount = currentState.discount.toDoubleOrNull(),
            vat = currentState.vat.toDoubleOrNull() ?: 0.0,
            sortOrder = currentState.sortOrder.toIntOrNull() ?: 0,
            categoryId = currentState.selectedCategoryId,
            businessId = businessId,
            stock = newStock,
            updatedBy = staffId,
            // We need to keep other fields consistent, nominally we'd re-fetch but this is a quick update
             // Ideally we should use a specific updateStockUseCase if available, but for now we update the whole product
             // preserving what we have in state which should be current.
            // CAUTION: This assumes the state matches the DB.
        )
        // We also need to preserve createdBy/createdAt if possible, or the backend handles it.
        // For safe update, we might want to use the object we loaded + new stock.
        // A better approach is to rely on backend partial update or re-fetch in real apps.
        // detailed implementation below uses the updateProductUseCase with the constructed product.

         viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isAddStockDialogOpen = false) }

            updateProductUseCase(product)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            stock = newStock.toString(),
                            currentStock = newStock,
                            successMessage = "Stock added successfully! New stock: $newStock"
                        )
                    }
                }
                .onError { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to add stock: $error"
                        )
                    }
                }
        }
    }

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



        val costPrice = currentState.costPrice.toDoubleOrNull() ?: 0.0
        // Optionally validate cost price is not negative
        if (costPrice < 0) {
            _uiState.update { it.copy(errorMessage = "Cost price cannot be negative") }
            return
        }


        val discount = currentState.discount.toDoubleOrNull()
        val vat = currentState.vat.toDoubleOrNull() ?: 0.0
        val sortOrder = currentState.sortOrder.toIntOrNull() ?: 0
        val stock = if(isEditMode) currentState.currentStock else (currentState.stock.toDoubleOrNull() ?: 0.0)


        val product = Product(
            id = if(isEditMode)currentState.productId!! else UUID.randomUUID().toString(), // Will be empty for create, set for update
            name = currentState.name,
            unitPrice = unitPrice,
            costPrice = costPrice,
            sku = currentState.sku,
            description = currentState.description,
            discount = discount,
            vat = vat,
            sortOrder = sortOrder,
            stock = stock,
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
                        it.copy(
                            isLoading = false,
                            successMessage = "Product ${if (isEditMode) "updated" else "created"} successfully!",
                            isSaved = true,
                            name = if (!isEditMode) "" else it.name,
                            unitPrice = if (!isEditMode) "" else it.unitPrice,
                            costPrice = if (!isEditMode) "" else it.costPrice,
                            sku = if (!isEditMode) "" else it.sku,
                            description = if (!isEditMode) "" else it.description,
                            discount = if (!isEditMode) "" else it.discount,
                            vat = if (!isEditMode) "0.0" else it.vat,
                            sortOrder = if (!isEditMode) "0" else it.sortOrder,
                            stock = if (!isEditMode) "" else it.stock,
                            selectedCategoryId = if (!isEditMode) "" else it.selectedCategoryId,
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