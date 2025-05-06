package com.ikemba.inventrar.changePassword.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikemba.inventrar.app.Route
import com.ikemba.inventrar.core.domain.onError
import com.ikemba.inventrar.core.domain.onSuccess
import com.ikemba.inventrar.core.presentation.NavigationViewModel
import com.ikemba.inventrar.user.domain.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel(){
    private val _state = MutableStateFlow( ChangePasswordState())
    val state = _state.asStateFlow()



    private fun changePassword() {
        viewModelScope.launch {
            userRepository.changePassword(state.value.oldPassword, state.value.newPassword, state.value.confirmPassword)
                .onSuccess { response ->

                    if(response?.responseCode == 0){
                        userRepository.deleteAllUsers()
                        NavigationViewModel.navController?.navigate(Route.Login)
                    }
                    else{
                        _state.update {
                            it.copy(errorMessage = response?.responseMessage.toString(), isLoading = false)
                        }
                    }
                    println("Amad "+ response)
                }
                .onError { error->
                    _state.update {
                        it.copy(errorMessage = error.toString(), isLoading = false) }

                }
        }
    }

    fun onAction(action: ChangePasswordAction){
        when(action){
            is ChangePasswordAction.OnChangePasswordButtonClicked ->{
                _state.update {
                    it.copy(errorMessage = "")
                }
                if(state.value.oldPassword.isEmpty()){
                    _state.update {
                        it.copy(errorMessage = "Please enter old password")
                    }
                    return
                }
                if(state.value.newPassword.isEmpty()){
                    _state.update {
                        it.copy(errorMessage = "Please enter new password")
                    }
                    return
                }
                if(state.value.newPassword != state.value.confirmPassword){
                    _state.update {
                        it.copy(errorMessage = "password and confirm password do not match")
                    }
                    return
                }
                _state.update {
                    it.copy(isLoading = true)
                }
               changePassword()

            }
            is ChangePasswordAction.OnPasswordChange ->{
                _state.update {
                    it.copy(password = action.password)
                }
            }
            is ChangePasswordAction.OnUserNameChange ->{

                _state.update {
                    it.copy(userName = action.userName)
                }
            }
            is ChangePasswordAction.OnOldPasswordChanged ->{

                _state.update {
                    it.copy(oldPassword = action.oldPassword)
                }
            }
            is ChangePasswordAction.OnNewPasswordChanged ->{

                _state.update {
                    it.copy(newPassword = action.newPassword)
                }
            }
            is ChangePasswordAction.OnConfirmPasswordChanged ->{

                _state.update {
                    it.copy(confirmPassword = action.confirmPassword)
                }
            }
            is ChangePasswordAction.OnToggleVisibility ->{

                _state.update {
                    it.copy(isVisible =true)
                }
            }
            else->{

            }
        }
    }
}