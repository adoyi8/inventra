package com.sundroid.kobokobo.business.presentation

data class BusinessState (
    val isLoading: Boolean = false,
    var isVisible  : Boolean = false,
    val errorMessage: String = "",
    var showConfirmLogout: Boolean = false,

)