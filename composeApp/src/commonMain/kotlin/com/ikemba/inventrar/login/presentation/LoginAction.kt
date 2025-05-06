package com.ikemba.inventrar.login.presentation

sealed interface LoginAction {
    data object OnLoginButtonClicked: LoginAction
    data class OnUserNameChange(val userName: String): LoginAction
    data class OnPasswordChange(val password: String): LoginAction
    data class OnToggleVisibility(val visibility: Boolean): LoginAction
}