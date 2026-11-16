package com.sundroid.kobokobo.login.presentation

import com.sundroid.kobokobo.admin.data.domain.model.Permission
import com.sundroid.kobokobo.user.domain.User

data class LoginState (
    val userName: String ="",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    var isVisible  : Boolean = false,
    val errorMessage: String = "",
    var showConfirmLogout: Boolean = false,
    val permissions: List<String> = emptyList(), // Permissions for the current user's role
)