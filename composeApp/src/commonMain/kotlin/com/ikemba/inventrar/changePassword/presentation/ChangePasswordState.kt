package com.ikemba.inventrar.changePassword.presentation

import com.ikemba.inventrar.user.domain.User

data class ChangePasswordState (
    val userName: String ="",
    val password: String = "",
    val oldPassword: String = "",
    val newPassword: String ="",
    val confirmPassword: String ="",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    var isVisible  : Boolean = false,
    val errorMessage: String = ""

)