package com.ikemba.inventrar.splashScreen.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikemba.inventrar.app.Route
import com.ikemba.inventrar.core.domain.onError
import com.ikemba.inventrar.core.domain.onSuccess
import com.ikemba.inventrar.core.presentation.NavigationViewModel
import com.ikemba.inventrar.core.presentation.toUiText
import com.ikemba.inventrar.dashboard.utils.Util
import com.ikemba.inventrar.user.domain.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashScreenViewModel(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle,
): ViewModel(){
    private val _state = MutableStateFlow(SplashScreenState())
    private var observeUsers: Job? = null

    val state = _state.asStateFlow()
        .onStart {
            delay(3000)
            observeUsers()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private fun observeUsers() {
        observeUsers?.cancel()
        observeUsers = userRepository
            .getAllUsers()
            .onEach { users ->
                _state.update { it.copy(
                    users = users
                ) }
                println("Alamo users ${users.size}")
                if(users.isEmpty()){
                    NavigationViewModel.navController?.navigate(Route.Login)
                }
                else{
                  Util.accessToken = users.last().accessToken.toString()
                   // NavigationViewModel.navController?.navigate(Route.Login)
                    NavigationViewModel.navController?.navigate(Route.POSScreen)
                }
            }
            .launchIn(viewModelScope)
    }



    private fun login() {
        viewModelScope.launch {
            userRepository.login(state.value.userName, state.value.password)
                .onSuccess { userResponse ->

                    if(userResponse?.responseCode == 0){
                        println("Liv 1")
                    }
                    else{
                        println("Liv 2")
                        _state.update {
                            it.copy(errorMessage = userResponse?.responseMessage.toString(), isLoading = false)
                        }
                    }
                    println("Amad "+ userResponse)
                }
                .onError { error->
                    println("Liv 3")
                    _state.update {
                        it.copy(errorMessage = error.toUiText().toString(), isLoading = false) }

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
                    return
                }
                if(state.value.password.isEmpty()){
                    _state.update {
                        it.copy(errorMessage = "Please enter your password")
                    }
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
}