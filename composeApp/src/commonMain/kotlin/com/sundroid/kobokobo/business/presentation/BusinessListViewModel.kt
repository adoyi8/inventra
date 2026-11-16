package com.sundroid.kobokobo.business.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sundroid.kobokobo.business.data.database.Business
import com.sundroid.kobokobo.business.data.database.BusinessDao
import com.sundroid.kobokobo.business.data.domain.useCase.CreateBusinessUseCase
import com.sundroid.kobokobo.business.data.domain.useCase.GetBusinessesForUserUseCase
import com.sundroid.kobokobo.business.data.dto.BusinessWithStatus
import com.sundroid.kobokobo.user.data.database.UserDao
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BusinessListUiState(
    val isLoading: Boolean = false,
    val businesses: List<BusinessWithStatus> = emptyList(),
    val error: String? = null,
    val selectedBusiness: Business? = null,
)

class BusinessListViewModel(
    private val getBusinessesForUserUseCase: GetBusinessesForUserUseCase,
    private val createBusinessUseCase: CreateBusinessUseCase,
    private val businessDao: BusinessDao,
    private val userDao: UserDao
    // Assuming you get this upon login
) : ViewModel()
{

    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val _uiState = MutableStateFlow(BusinessListUiState())
    val uiState = _uiState.asStateFlow()

    private var businessesJob: Job? = null

    init {
        currentUser?.uid?.let { startListeningForBusinesses(it) }
    }

//    fun loadBusinesses(id: String? = FirebaseAuth.getInstance().currentUser?.uid) {
//        println("amirah "+currentUser?.uid)
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true) }
//            id?.let { it ->
//                getBusinessesForUserUseCase(it)
//                    .onSuccess { businesses ->
//                        _uiState.update {
//                            it.copy(isLoading = false, businesses = businesses.filter {business-> business.active}, error = null)
//                        }
//                    }
//                    .onFailure { error ->
//                        println("bikini "+ error.message)
//                        _uiState.update {
//                            it.copy(isLoading = false, error = "Failed to load businesses.")
//                        }
//                    }
//            }
//        }
//    }
    fun startListeningForBusinesses(userId: String) {
        // Cancel any existing job to prevent multiple listeners
        businessesJob?.cancel()

        businessesJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getBusinessesForUserUseCase(userId)
                .catch { error ->
                    // Handle errors from the Flow (e.g., permission issues)
                    println("Error listening for businesses: ${error.message}")
                    _uiState.update {
                        it.copy(isLoading = false, error = "Failed to load businesses.")
                    }
                }
                .collect { businessWithStatuses ->
                    // This block runs every time Firestore data changes
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            // The filter business.active is now done in the UseCase/Repository
                            businesses = businessWithStatuses,
                            error = null
                        )
                    }
                }
        }
    }
    fun createBusiness(name: String, address: String, features: List<String>) {
        viewModelScope.launch {
            currentUser?.let {
                println("shiva 1")
                createBusinessUseCase(name, address, features, it)
                    .onSuccess {
                         // Refresh the list after creation
                        _uiState.update { it.copy(isLoading = false) }
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