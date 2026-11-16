package com.sundroid.kobokobo.login.presentation

sealed interface LoginAction {
    data class OnToggleVisibility(val visibility: Boolean): LoginAction
    data object OnGoogleLoginClicked : LoginAction // New action
    data class SetVisible(val isVisible: Boolean) : LoginAction // New action
}