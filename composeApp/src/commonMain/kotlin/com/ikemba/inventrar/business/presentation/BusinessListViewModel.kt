package com.ikemba.inventrar.business.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Entity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ikemba.inventrar.business.data.database.Business
import com.ikemba.inventrar.business.data.database.BusinessDao
import com.ikemba.inventrar.business.data.domain.model.AppUser
import com.ikemba.inventrar.business.data.domain.useCase.CreateBusinessUseCase
import com.ikemba.inventrar.business.data.domain.useCase.GetBusinessesForUserUseCase
import com.ikemba.inventrar.user.data.database.UserDao
import com.ikemba.inventrar.user.data.database.UserEntity

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BusinessListUiState(
    val isLoading: Boolean = false,
    val businesses: List<Business> = emptyList(),
    val error: String? = null,
    val selectedBusiness: Business? = null,
)

class BusinessListViewModel(
    private val getBusinessesForUserUseCase: GetBusinessesForUserUseCase,
    private val createBusinessUseCase: CreateBusinessUseCase,
    private val businessDao: BusinessDao,
    private val userDao: UserDao
    // Assuming you get this upon login
) : ViewModel() {

    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val _uiState = MutableStateFlow(BusinessListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadBusinesses()
    }

    fun loadBusinesses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            currentUser?.let {
                getBusinessesForUserUseCase(it.uid)
                    .onSuccess { businesses ->
                        _uiState.update {
                            it.copy(isLoading = false, businesses = businesses, error = null)
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(isLoading = false, error = "Failed to load businesses.")
                        }
                    }
            }
        }
    }

    fun createBusiness(name: String) {
        viewModelScope.launch {
            currentUser?.let {
                createBusinessUseCase(name, it)
                    .onSuccess {
                        loadBusinesses() // Refresh the list after creation
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(error = "Failed to create business.") }
                    }
            }
        }
    }

    fun selectBusiness1(business: Business) {
        viewModelScope.launch {
            businessDao.upsert(business)
            _uiState.update { it.copy(selectedBusiness = business) }
        }

    }
}