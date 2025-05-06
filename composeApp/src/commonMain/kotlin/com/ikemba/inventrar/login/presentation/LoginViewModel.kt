package com.ikemba.inventrar.login.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikemba.inventrar.app.Route
import com.ikemba.inventrar.core.domain.onError
import com.ikemba.inventrar.core.domain.onSuccess
import com.ikemba.inventrar.core.presentation.NavigationViewModel
import com.ikemba.inventrar.dashboard.utils.Util
import com.ikemba.inventrar.user.data.mappers.toUser
import com.ikemba.inventrar.user.domain.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel(){
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()
    private fun login() {
        viewModelScope.launch {
            userRepository.login(state.value.userName.trim(), state.value.password.trim())
                .onSuccess { userResponse ->

                    if(userResponse?.responseCode == 0){
                        Util.accessToken = userResponse.toUser().accessToken!!
                        userRepository.deleteAllUsers()
                        userRepository.saveUserInLocalDb(userResponse.toUser())
                        NavigationViewModel.navController?.navigate(Route.POSScreen)
                    }
                    else{
                        _state.update {
                            it.copy(errorMessage = userResponse?.responseMessage.toString(), isLoading = false)
                        }
                        hideErrorMessageAfterDelay()
                    }
                    println("Amad "+ userResponse)
                }
                .onError { error->
                    _state.update {
                        it.copy(errorMessage = error.toString(), isLoading = false)
                    }
                    hideErrorMessageAfterDelay()
                }
        }
    }

    fun onAction(action: LoginAction){
        when(action){
            is LoginAction.OnLoginButtonClicked ->{
                _state.update {
                    it.copy(errorMessage = "")
                }
                if(state.value.userName.isEmpty()){
                    _state.update {
                        it.copy(errorMessage = "Please enter your username")
                    }
                    hideErrorMessageAfterDelay()
                    return
                }
                if(state.value.password.isEmpty()){
                    _state.update {
                        it.copy(errorMessage = "Please enter your password")
                    }
                    hideErrorMessageAfterDelay()
                    return
                }
                _state.update {
                    it.copy(isLoading = true)
                }

               login()

            }
            is LoginAction.OnPasswordChange ->{
                _state.update {
                    it.copy(password = action.password)
                }
            }
            is LoginAction.OnUserNameChange ->{

                _state.update {
                    it.copy(userName = action.userName)
                }
            }
            is LoginAction.OnToggleVisibility ->{

                _state.update {
                    it.copy(isVisible =!it.isVisible)
                }
            }
            else->{

            }
        }
    }
    fun hideErrorMessageAfterDelay(){
        viewModelScope.launch {
            delay(4000)
            _state.update {
                it.copy(errorMessage = "")
            }
        }
    }
}