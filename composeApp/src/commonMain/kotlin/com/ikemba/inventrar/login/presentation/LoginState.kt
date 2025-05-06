package com.ikemba.inventrar.login.presentation

import com.ikemba.inventrar.user.domain.User

data class LoginState (
    val userName: String ="",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    var isVisible  : Boolean = false,
    val errorMessage: String = ""

)