package com.ikemba.inventrar.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikemba.inventrar.admin.data.domain.useCase.GetRolePermissionsList
import com.ikemba.inventrar.app.Route
import com.ikemba.inventrar.auth.GoogleAuthUiProvider
import com.ikemba.inventrar.auth.toUser
import com.ikemba.inventrar.business.data.database.Business
import com.ikemba.inventrar.business.data.domain.BusinessRepository
import com.ikemba.inventrar.business.data.domain.useCase.GetStaffRoleUseCase
import com.ikemba.inventrar.business.data.dto.CreateBusinessRequest
import com.ikemba.inventrar.business.data.dto.SearchOrganizationRequest
import com.ikemba.inventrar.core.domain.onError
import com.ikemba.inventrar.core.domain.onSuccess
import com.ikemba.inventrar.core.presentation.NavigationViewModel
import com.ikemba.inventrar.dashboard.domain.ProductRepository
import com.ikemba.inventrar.dashboard.utils.Util
import com.ikemba.inventrar.user.data.mappers.toUser
import com.ikemba.inventrar.user.domain.User
import com.ikemba.inventrar.user.domain.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class UserViewModel(
    private val userRepository: UserRepository,
    private val businessRepository: BusinessRepository,
    private val getStaffRoleUseCase: GetStaffRoleUseCase,
    private val getRolePermissionsList: GetRolePermissionsList,
    private val productRepository: ProductRepository
): ViewModel()
{
    private val _state = MutableStateFlow(LoginState())

    private val _userState = MutableStateFlow(UserState())
     var googleAuthUIProvider : GoogleAuthUiProvider? = null
    private var observeUsers: Job? = null

    val state = _state.asStateFlow()
    
    init {
        observeUsers()
    }
    val userState = _userState.asStateFlow()
    private fun login() {
        viewModelScope.launch {
            userRepository.login(_state.value.userName.trim(), _state.value.password.trim())
                .onSuccess { userResponse ->

                    if(userResponse?.responseCode == 0){
                        Util.accessToken = userResponse.employee?.token!!
                        userRepository.deleteAllUsers()
                        userRepository.saveUserInLocalDb(userResponse.toUser())
                        _state.update {
                            it.copy( isLoading = false, userName = "", password = "")
                        }
                        NavigationViewModel.navController?.navigate(Route.UserProfileRoute)
                    }
                    else{
                        _state.update {
                            it.copy(errorMessage = userResponse?.responseMessage.toString(), isLoading = false)
                        }
                        hideErrorMessageAfterDelay()
                    }

                }
                .onError { error->
                    _state.update {
                        it.copy(errorMessage = error.toString(), isLoading = false)
                    }
                    hideErrorMessageAfterDelay()
                }
        }
    }
     fun getBusinesses(searchOrganization: SearchOrganizationRequest) {
        viewModelScope.launch {
            businessRepository.getBusinesses(Util.accessToken, searchOrganization)
                .onSuccess { businessResponse ->

                    if(businessResponse.responseCode == 0){
                        _state.update {
                            it.copy( isLoading = false, password = "")
                        }
                        businessResponse.organizations?.let { business->
                            _userState.update {
                                it.copy(business = business)
                            }
                        }
                    }
                    else{
                        _state.update {
                            it.copy(errorMessage = businessResponse?.responseMessage.toString(), isLoading = false)
                        }
                        hideErrorMessageAfterDelay()
                    }

                }
                .onError { error->
                    _state.update {
                        it.copy(errorMessage = error.toString(), isLoading = false)
                    }
                    hideErrorMessageAfterDelay()
                }
        }
    }

    private fun createBusinesses(createBusinessRequest: CreateBusinessRequest) {
        viewModelScope.launch {
            businessRepository.createBusiness(Util.accessToken, createBusinessRequest)
                .onSuccess { createBusinessResponse ->

                    if(createBusinessResponse.responseCode == 0){
                        _state.update {
                            it.copy( isLoading = false, password = "")
                        }
                    }
                    else{
                        _state.update {
                            it.copy(errorMessage = createBusinessResponse?.responseMessage.toString(), isLoading = false)
                        }
                        hideErrorMessageAfterDelay()
                    }

                }
                .onError { error->
                    _state.update {
                        it.copy(errorMessage = error.toString(), isLoading = false)
                    }
                    hideErrorMessageAfterDelay()
                }
        }
    }

    private fun observeUsers() {
        observeUsers?.cancel()
        observeUsers = userRepository
            .getAllUsers() // Ensure your repository maps the Entity's permission list to the User model
            .onEach { users ->
                if (users.isNotEmpty()) {
                    val userFromDb = users.first()

                    _state.update { it ->
                        it.copy(
                            currentUser = userFromDb,
                            // Load permissions from the DB object into the main state
                            permissions = userFromDb.permissions
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }
    fun getUser(): User? {

        return (_state.value.currentUser)
    }
    fun logout() {
        viewModelScope.launch {
            hideShowConfirmLogout()
            userRepository.deleteAllUsers()
            googleAuthUIProvider?.signOut()
            _state.update {
                LoginState()
            }
        }
    }
    fun hideShowConfirmLogout() {
        _state.update {
            it.copy(showConfirmLogout = false)
        }
    }
    fun showShowConfirmLogout() {
        viewModelScope.launch {
            _state.update {
                it.copy(showConfirmLogout = true)
            }
        }
    }
    fun onAction(action: LoginAction){
        when(action){
            is LoginAction.OnLoginButtonClicked ->{
                _state.update {
                    it.copy(errorMessage = "")
                }
                if(_state.value.userName.isEmpty()){
                    _state.update {
                        it.copy(errorMessage = "Please enter your username")
                    }
                    hideErrorMessageAfterDelay()
                    return
                }
                if(_state.value.password.isEmpty()){
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
            LoginAction.OnGoogleLoginClicked -> {
                viewModelScope.launch {
                    try {
                        val firebaseUser = googleAuthUIProvider?.signIn()
                        if (firebaseUser != null) {
                            // --- THIS IS THE NEW LOGIC ---
                            userRepository.checkAndCreateUserInFirestore(firebaseUser)
                                .onSuccess {
                                    // Logic to run AFTER user is confirmed to exist in Firestore
                                    userRepository.deleteAllUsers()
                                    userRepository.saveUserInLocalDb(firebaseUser.toUser())
                                    _state.update {
                                        it.copy(isLoading = false, userName = "", password = "")
                                    }
                                    NavigationViewModel.navController?.navigate(Route.UserProfileRoute)
                                }
                                .onFailure { error ->
                                    // Handle failure to create user in Firestore
                                    _state.update {
                                        it.copy(errorMessage = error.toString(), isLoading = false)
                                    }
                                    hideErrorMessageAfterDelay()
                                }
                            // --- END OF NEW LOGIC ---
                        } else {
                            _state.update {
                                it.copy(errorMessage = "Google Sign-In failed. Please try again.", isLoading = false)
                            }
                            hideErrorMessageAfterDelay()
                        }
                    } catch (e: Exception) {
                        _state.update {
                            it.copy(errorMessage = e.message ?: "An unexpected error occurred.", isLoading = false)
                        }
                        hideErrorMessageAfterDelay()
                    }
                }
            }
            is LoginAction.SetVisible -> {
                _state.update { it.copy(isVisible = action.isVisible) }
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

    fun setSelectedBusiness(result: Business) {
        viewModelScope.launch {
            val currentUser = getUser() ?: return@launch

            // 1. Basic update with business info
            val userWithBusiness = currentUser.copy(
                businessId = result.businessId,
                businessName = result.name
            )

            // Update UI immediately for responsiveness (optional, but keeps UI snappy)
            _state.update { it.copy(currentUser = userWithBusiness) }

            // 2. Fetch Role
            getStaffRoleUseCase(currentUser.id, result.businessId).onSuccess { staff ->
                val roleId = staff?.role

                if (roleId != null) {
                    // 3. Fetch Permissions using the Role ID
                    getRolePermissionsList(roleId, businessId = result.businessId)
                        .onSuccess { permissions ->

                            // 4. Create final user object WITH Role AND Permissions
                            val finalUser = userWithBusiness.copy(
                                roleName = staff.role, // or role name if you have it
                                staffNo = staff.staffId,
                                roleId = roleId,
                                permissions = permissions // <--- STORE PERMISSIONS IN USER
                            )

                            // 5. Save to DB (Now persists permissions)
                            userRepository.saveUserInLocalDb(finalUser)

                            // 6. Update State
                            _state.update {
                                it.copy(
                                    currentUser = finalUser,
                                    permissions = permissions
                                )
                            }
                        }
                        .onError {
                            // Handle permission fetch failure
                            saveUserAndSetEmptyPermissions(userWithBusiness, staff.staffId, roleId)
                        }
                } else {
                    // Role not found
                    saveUserAndSetEmptyPermissions(userWithBusiness, null, null)
                }
            }.onFailure {
                // Failed to get staff role
                userRepository.saveUserInLocalDb(userWithBusiness)
                _state.update { it.copy(currentUser = userWithBusiness, permissions = emptyList()) }
            }
        }
    }

    // Helper to reduce code duplication
    private suspend fun saveUserAndSetEmptyPermissions(user: User, staffNo: String?, roleId: String?) {
        val updatedUser = user.copy(staffNo = staffNo, roleId = roleId, permissions = emptyList())
        userRepository.saveUserInLocalDb(updatedUser)
        _state.update {
            it.copy(currentUser = updatedUser, permissions = emptyList())
        }
    }

    fun exitBusiness() {
        viewModelScope.launch {
            val currentUser = getUser() ?: return@launch
            val clearedUser = currentUser.copy(
                businessId = null,
                businessName = null,
                roleName = null
            )
            userRepository.saveUserInLocalDb(clearedUser)
            productRepository.clear()
            // Update state so UI reacts immediately and clear permissions
            _state.update { 
                it.copy(
                    currentUser = clearedUser,
                    permissions = emptyList()
                )
            }
        }
    }
    
    /**
     * Check if the current user has a specific permission
     * @param permissionId The ID of the permission to check (e.g., "view_inventory", "add_products")
     * @return true if the user has the permission, false otherwise
     */
    fun hasPermission(permissionId: String): Boolean {
        return _state.value.permissions.contains(permissionId)
    }
    
    /**
     * Check if the current user has any of the specified permissions
     * @param permissionIds List of permission IDs to check
     * @return true if the user has at least one of the permissions, false otherwise
     */
    fun hasAnyPermission(permissionIds: List<String>): Boolean {
        val userPermissionIds = _state.value.permissions.map { it }.toSet()
        return permissionIds.any { it in userPermissionIds }
    }
    
    /**
     * Check if the current user has all of the specified permissions
     * @param permissionIds List of permission IDs to check
     * @return true if the user has all of the permissions, false otherwise
     */
    fun hasAllPermissions(permissionIds: List<String>): Boolean {
        val userPermissionIds = _state.value.permissions.map { it }.toSet()
        return permissionIds.all { it in userPermissionIds }
    }
    
    /**
     * Get all permission IDs for the current user
     * @return List of permission IDs
     */
    fun getPermissionIds(): List<String> {
        return _state.value.permissions.map { it }
    }
}