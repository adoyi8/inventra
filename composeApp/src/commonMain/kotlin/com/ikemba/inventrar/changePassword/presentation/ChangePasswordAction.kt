package com.ikemba.inventrar.changePassword.presentation

sealed interface ChangePasswordAction {
    data object OnChangePasswordButtonClicked: ChangePasswordAction
    data class OnUserNameChange(val userName: String): ChangePasswordAction
    data class OnPasswordChange(val password: String): ChangePasswordAction
    data class OnOldPasswordChanged(val oldPassword: String): ChangePasswordAction
    data class OnNewPasswordChanged(val newPassword: String): ChangePasswordAction
    data class OnConfirmPasswordChanged(val confirmPassword: String): ChangePasswordAction
    data class OnToggleVisibility(val visibility: Boolean): ChangePasswordAction
}