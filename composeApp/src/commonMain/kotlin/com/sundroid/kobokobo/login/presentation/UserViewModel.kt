package com.sundroid.kobokobo.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import com.sundroid.kobokobo.admin.data.domain.useCase.GetRolePermissionsList
import com.sundroid.kobokobo.app.Route
import com.sundroid.kobokobo.auth.GoogleAuthUiProvider
import com.sundroid.kobokobo.auth.toUser
import com.sundroid.kobokobo.business.data.database.Business
import com.sundroid.kobokobo.business.data.domain.BusinessRepository
import com.sundroid.kobokobo.business.data.domain.useCase.GetStaffRoleUseCase
import com.sundroid.kobokobo.business.data.dto.CreateBusinessRequest
import com.sundroid.kobokobo.business.data.dto.SearchOrganizationRequest
import com.sundroid.kobokobo.core.domain.onError
import com.sundroid.kobokobo.core.domain.onSuccess
import com.sundroid.kobokobo.core.presentation.NavigationViewModel
import com.sundroid.kobokobo.dashboard.domain.ProductRepository
import com.sundroid.kobokobo.dashboard.utils.Util
import com.sundroid.kobokobo.user.data.mappers.toUser
import com.sundroid.kobokobo.user.domain.User
import com.sundroid.kobokobo.user.domain.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
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

    private var businessListenerJob: Job? = null
    private var staffListenerJob: Job? = null
    private var hasInitializedOnStart = false



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

                    val businessId = userFromDb.businessId
                    if (!hasInitializedOnStart && businessId != null) {
                        hasInitializedOnStart = true
                        refreshPermissions()
                        startBusinessListener(businessId)
                        startStaffListener(userFromDb.id, businessId)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
    fun getUser(): User? {

        return (_state.value.currentUser)
    }
    fun logout() {
        businessListenerJob?.cancel()
        staffListenerJob?.cancel()
        hasInitializedOnStart = false
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
                _state.update {
                    it.copy(isLoading = true)
                }
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
            businessListenerJob?.cancel()
            val currentUser = getUser() ?: return@launch

            // 1. Basic update with business info
            println("Cape verde "+ result.features.size)
            val userWithBusiness = currentUser.copy(
                businessId = result.businessId,
                businessName = result.name,
                businessFeatures = result.features,
            )

            // Update UI immediately for responsiveness (optional, but keeps UI snappy)
            _state.update { it.copy(currentUser = userWithBusiness) }

            // 2. Fetch Role
            getStaffRoleUseCase(currentUser.id, result.businessId).onSuccess { staff ->
                val roleId = staff?.roleId

                if (roleId != null) {
                    // 3. Fetch Permissions using the Role ID
                    getRolePermissionsList(roleId, businessId = result.businessId)
                        .onSuccess { permissions ->

                            // 4. Create final user object WITH Role AND Permissions
                            val finalUser = userWithBusiness.copy(
                                roleName = staff.roleId, // or role name if you have it
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
            startBusinessListener(result.businessId)
            startStaffListener(currentUser.id, result.businessId)
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

    private fun Throwable.isRetryableFirestoreUnavailable(): Boolean {
        return this is FirebaseFirestoreException &&
            code == FirebaseFirestoreException.Code.UNAVAILABLE
    }

    private fun startBusinessListener(businessId: String) {
        businessListenerJob = viewModelScope.launch {
            businessRepository.getBusinessFlow(businessId)
                .retryWhen { cause, attempt ->
                    if (cause.isRetryableFirestoreUnavailable()) {
                        delay(((attempt + 1) * 1_000L).coerceAtMost(5_000L))
                        true
                    } else {
                        false
                    }
                }
                .catch { error ->
                    println("Business listener stopped: ${error.message}")
                }
                .collect { business ->
                    if (business == null || !business.active) {
                        // Business is either deleted or marked as inactive.
                        // Exit the business to trigger navigation to GlobalNavGraph.
                        println("Business $businessId is inactive or deleted. Exiting.")
                        exitBusiness()
                        // Cancel this collection job as it's no longer needed
                        cancel()
                    } else {
                        // Business is active. You could optionally update the
                        // businessName in userState if it changed.
                        if (_state.value.currentUser?.businessName != business.name) {
                            val updatedUser = _state.value.currentUser?.copy(businessName = business.name)
                            if (updatedUser != null) {
                                userRepository.saveUserInLocalDb(updatedUser)
                                _state.update { it.copy(currentUser = updatedUser) }
                            }
                        }
                    }
                }
        }
    }
    private fun startStaffListener(userId: String, businessId: String) {
        staffListenerJob = viewModelScope.launch {
            userRepository.getMyStaffEntryFlow(userId, businessId)
                .retryWhen { cause, attempt ->
                    if (cause.isRetryableFirestoreUnavailable()) {
                        delay(((attempt + 1) * 1_000L).coerceAtMost(5_000L))
                        true
                    } else {
                        false
                    }
                }
                .catch { error ->
                    println("Staff listener stopped: ${error.message}")
                }
                .collect { staff ->
                    // If the staff record is deleted (null) OR marked as inactive
                    if (staff == null || !staff.active) {
                        println("Staff record inactive or deleted for this business. Exiting.")
                        // This will clear the state and trigger navigation to GlobalNavGraph
                        exitBusiness()
                    } else {
                        // Optional: If their role changed in real-time, you could update permissions here
                        if(_state.value.currentUser?.roleName != staff.roleId) {
                            // Trigger a reload of permissions and user role info
                        }
                    }
                }
        }
    }

    fun exitBusiness() {
        businessListenerJob?.cancel()
        staffListenerJob?.cancel()
        hasInitializedOnStart = false
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
    fun refreshPermissions() {
        viewModelScope.launch {
            val currentUser = getUser() ?: return@launch
            val businessId = currentUser.businessId ?: return@launch
            val roleId = currentUser.roleId ?: return@launch

            getRolePermissionsList(roleId, businessId)
                .onSuccess { permissions ->
                    val finalUser = currentUser.copy(
                        permissions = permissions
                    )
                    userRepository.saveUserInLocalDb(finalUser)
                    _state.update {
                        it.copy(
                            currentUser = finalUser,
                            permissions = permissions
                        )
                    }
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
