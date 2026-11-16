package com.sundroid.kobokobo.damages.presentation

import com.sundroid.kobokobo.damages.data.domain.Damage
import com.sundroid.kobokobo.inventory.data.domain.model.Location
import com.sundroid.kobokobo.inventory.data.domain.model.Product



data class DamageState(
    // For the list screen
    val damages: List<Damage> = emptyList(),
    val isLoadingList: Boolean = false,
    val listErrorMessage: String? = null,

    // For the Add Dialog
    val isSubmitting: Boolean = false,
    val isSubmitSuccess: Boolean = false,
    val submitErrorMessage: String? = null,
    val allProducts: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val searchQuery: String = "",
    val selectedProduct: Product? = null,
    val quantity: String = "",
    val reason: String = "",
    val showAddDialog: Boolean = false
) {
    val canSubmit: Boolean
        get() = selectedProduct != null &&
                quantity.toIntOrNull()?.let { it > 0 } == true &&
                !isSubmitting
}