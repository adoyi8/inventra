import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ikemba.inventrar.business.presentation.AddStaffViewModel
import com.ikemba.inventrar.core.presentation.components.CustomText


// val viewModel: AddStaffViewModel = koinInject()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStaffScreen(viewModel: AddStaffViewModel, title: MutableState<String>) {
    val uiState by viewModel.uiState.collectAsState()
    var roleExpanded by remember { mutableStateOf(false) }
    var deptExpanded by remember { mutableStateOf(false) }
    var branchExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        title.value= "Add New Staff"

        OutlinedTextField(
            value = uiState.emailQuery,
            onValueChange = { viewModel.onEmailQueryChanged(it) },
            label = { CustomText("Staff's Email") },
            modifier = Modifier.fillMaxWidth()
        )

        // Role dropdown
        ExposedDropdownMenuBox(
            expanded = roleExpanded,
            onExpandedChange = { roleExpanded = !roleExpanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = uiState.selectedRole,
                onValueChange = {},
                label = { CustomText("Select Role") },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) }
            )
            ExposedDropdownMenu(
                expanded = roleExpanded,
                onDismissRequest = { roleExpanded = false }
            ) {
                uiState.roles.forEach { role -> // <-- Use uiState.roles
                    DropdownMenuItem(
                        text = { CustomText(role) },
                        onClick = {
                            viewModel.onRoleSelected(role)
                            roleExpanded = false
                        }
                    )
                }
            }
        }

        // Department dropdown
        ExposedDropdownMenuBox(
            expanded = deptExpanded,
            onExpandedChange = { deptExpanded = !deptExpanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = uiState.departments.firstOrNull { it.first == uiState.selectedDepartmentId }?.second ?: "",
                onValueChange = {},
                label = { CustomText("Select Department") },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) }
            )
            ExposedDropdownMenu(
                expanded = deptExpanded,
                onDismissRequest = { deptExpanded = false }
            ) {
                uiState.departments.forEach { (id, name) ->
                    DropdownMenuItem(
                        text = { CustomText(name) },
                        onClick = {
                            viewModel.onDepartmentSelected(id)
                            deptExpanded = false
                        }
                    )
                }
            }
        }

        // Branch dropdown
        ExposedDropdownMenuBox(
            expanded = branchExpanded,
            onExpandedChange = { branchExpanded = !branchExpanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = uiState.branches.firstOrNull { it.first == uiState.selectedBranchId }?.second ?: "",
                onValueChange = {},
                label = { CustomText("Select Branch") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = branchExpanded) }
            )
            ExposedDropdownMenu(
                expanded = branchExpanded,
                onDismissRequest = { branchExpanded = false }
            ) {
                uiState.branches.forEach { (id, name) ->
                    DropdownMenuItem(
                        text = { CustomText(name) },
                        onClick = {
                            viewModel.onBranchSelected(id)
                            branchExpanded = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = { viewModel.verifyEmail() },
            enabled = uiState.emailQuery.isNotBlank() && !uiState.isLoading
        ) { CustomText("Verify Email") }

        if (uiState.isLoading) {
            CircularProgressIndicator()
        }

        uiState.error?.let {
            CustomText(it, color = MaterialTheme.colorScheme.error)
        }

        uiState.infoMessage?.let {
            CustomText(it, color = MaterialTheme.colorScheme.primary)
        }

        // Show user info and add button once user is found
        uiState.foundUser?.let { user ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    CustomText("User Found!", size = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomText("Name: ${user.fullName}")
                    CustomText("Email: ${user.email}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.addStaff(uiState.selectedRole) },
                        enabled = uiState.selectedRole.isNotBlank()
                                && uiState.selectedDepartmentId.isNotBlank()
                                && uiState.selectedBranchId.isNotBlank()
                                && !uiState.isLoading // <-- Disable button while adding
                    ) { CustomText("Add Staff") }
                }
            }
        }
    }
}