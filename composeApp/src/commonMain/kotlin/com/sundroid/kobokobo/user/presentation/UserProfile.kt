package com.sundroid.kobokobo.user.presentation

/*
 * App.kt
 * This file contains the entire application logic for the Shrine-inspired profile page,
 * updated to use Material 3 components.
 */

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
// Material 3 imports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.sundroid.kobokobo.business.presentation.BusinessViewModel
import com.sundroid.kobokobo.core.presentation.components.CustomText
import com.sundroid.kobokobo.core.presentation.components.ProgressDialog
import com.sundroid.kobokobo.dashboard.presentation.components.BlurredImageBackground
import com.sundroid.kobokobo.dropdowndata.data.dto.City
import com.sundroid.kobokobo.dropdowndata.data.dto.Country
import com.sundroid.kobokobo.dropdowndata.data.dto.OrganizationType
import com.sundroid.kobokobo.dropdowndata.data.dto.State
import com.sundroid.kobokobo.dropdowndata.presentation.DropDownSettingsViewModel

import com.sundroid.kobokobo.login.presentation.UserViewModel


@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        BlurredImageBackground(
            imageUrl = FirebaseAuth.getInstance().currentUser?.photoUrl.toString(),
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomText(FirebaseAuth.getInstance().currentUser?.displayName?:"", style = 20.sp, fontWeight = FontWeight.Bold)
        CustomText(FirebaseAuth.getInstance().currentUser?.email?:"", color = Color.Gray)
        CustomText(FirebaseAuth.getInstance().currentUser?.uid?:"", color = Color.Gray)
        CustomText(FirebaseAuth.getInstance().currentUser?.phoneNumber?:"", color = Color.Gray)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessCreationForm(businessViewModel: BusinessViewModel, dropDownSettingsViewModel: DropDownSettingsViewModel, userViewModel: UserViewModel) {

   // val businessState = businessViewModel.state.collectAsStateWithLifecycle()
    val createBusinessFormState = businessViewModel.createBusinessFormState.collectAsStateWithLifecycle()
    val dropDownState = dropDownSettingsViewModel.state.collectAsStateWithLifecycle()
    val businessState = businessViewModel.state.collectAsStateWithLifecycle()
    var businessName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }


    LaunchedEffect(true){
        dropDownSettingsViewModel.getCountries(Country())
        dropDownSettingsViewModel.getOrganizationType(OrganizationType())
    }


    // Update states when country changes
    LaunchedEffect(createBusinessFormState.value.selectedCountry) {

        if(createBusinessFormState.value.selectedCountry!=null){
            dropDownSettingsViewModel.getState(State(
                countryId = createBusinessFormState.value.selectedCountry!!.countryId,
                stateId = null,
                stateName = null,
                voided = null,
                createdBy = null,
                updatedBy = null,
                voidedBy = null,
                dateCreated = null,
                dateUpdated = null,
                dateVoided = null
            ))
        }

      //  selectedState = states[selectedCountry]?.get(0) ?: ""
    }
    // Update cities when state changes
    LaunchedEffect(createBusinessFormState.value.selectedState) {
        if(createBusinessFormState.value.selectedState!=null){
            dropDownSettingsViewModel.getCity(City(
                stateId = createBusinessFormState.value.selectedState!!.stateId,
                cityId = null,
                cityName = null,
                countryId = null,
                stateName = null,
                voided = null,
                createdBy = null,
                updatedBy = null,
                voidedBy = null,
                dateCreated = null,
                dateUpdated = null,
                dateVoided = null
            ))
        }

    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            , // Make form scrollable
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = true, // Can be toggled
            enter = slideInVertically(initialOffsetY = { -40 }) + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = createBusinessFormState.value.businessName,
                    onValueChange = { businessViewModel.updateBusinessName(it) },
                    label = { Text("Business Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                AnimatedVisibility(visible =  dropDownState.value.organizationTypes.isNotEmpty(), enter = scaleIn(), exit = scaleOut()) {

                        OrganizationTypeDropdownField(
                            label = "Business Type",
                            options = dropDownState.value.organizationTypes,
                            selectedOption = dropDownState.value.organizationTypes.first(),
                            onOptionSelected = { businessViewModel.updateSelectedBusinessType(it) }
                        )

                }
                // Business Type Dropdown

                Spacer(Modifier.height(16.dp))

                if(dropDownState.value.countries.isNotEmpty()) {
                    // Country Dropdown
                    DropdownField(
                        "Country",
                        dropDownState.value.countries,
                        dropDownState.value.countries.first()
                    ) { businessViewModel.updateSelectedCountry(it)}
                    Spacer(Modifier.height(16.dp))
                }

                if(dropDownState.value.states.isNotEmpty()) {
                    // Country Dropdown
                    StateDropdownField(
                        "State",
                        dropDownState.value.states,
                        dropDownState.value.states.first()
                    ) { businessViewModel.updateSelectedState(it)}
                    Spacer(Modifier.height(16.dp))
                }
                if(dropDownState.value.cities.isNotEmpty()) {
                    // Country Dropdown
                    CityDropdownField(
                        "City",
                        dropDownState.value.cities,
                        dropDownState.value.cities.first()
                    ) { businessViewModel.updateSelectedCity(it)}
                    Spacer(Modifier.height(16.dp))
                }

                // State Dropdown
              //  DropdownField("State", states[selectedCountry] ?: emptyList(), selectedState) { selectedState = it }
               // Spacer(Modifier.height(16.dp))

                // City Dropdown
                //DropdownField("City", cities[selectedState] ?: emptyList(), selectedCity) { selectedCity = it }
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = createBusinessFormState.value.address,
                    onValueChange = { businessViewModel.updateBusinessAddress(it) },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                OutlinedTextField(
                    value = createBusinessFormState.value.description,
                    onValueChange = { businessViewModel.updateBusinessDescription(it) },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                CustomText(businessState.value.errorMessage, color = Color.Red)
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        println("userId "+ userViewModel.getUser()?.userId)
                        userViewModel.getUser()?.let{

                            it.userId?.let { userId ->
                                businessViewModel.validateThenCreateBusiness(userId)
                            }
                        }

                              },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Create Business", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
    AnimatedVisibility(visible = businessState.value.isLoading || dropDownState.value.isLoading, enter = scaleIn(), exit = scaleOut()){
        ProgressDialog("Processing...")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    options: List<Country>,
    selectedOption: Country,
    onOptionSelected: (Country) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var currentSelection by remember { mutableStateOf(selectedOption) }

    // Update internal state if the external selectedOption changes
    LaunchedEffect(selectedOption) {
        currentSelection = selectedOption
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = currentSelection.countryName!!,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.countryName!!) },
                    onClick = {
                        currentSelection = option
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StateDropdownField(
    label: String,
    options: List<State>,
    selectedOption: State,
    onOptionSelected: (State) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var currentSelection by remember { mutableStateOf(selectedOption) }

    // Update internal state if the external selectedOption changes
    LaunchedEffect(selectedOption) {
        currentSelection = selectedOption
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = currentSelection.stateName!!,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.stateName!!) },
                    onClick = {
                        currentSelection = option
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDropdownField(
    label: String,
    options: List<City>,
    selectedOption: City,
    onOptionSelected: (City) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var currentSelection by remember { mutableStateOf(selectedOption) }

    // Update internal state if the external selectedOption changes
    LaunchedEffect(selectedOption) {
        currentSelection = selectedOption
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = currentSelection.cityName!!,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.cityName!!) },
                    onClick = {
                        currentSelection = option
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationTypeDropdownField(
    label: String,
    options: List<OrganizationType>,
    selectedOption: OrganizationType,
    onOptionSelected: (OrganizationType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var currentSelection by remember { mutableStateOf(selectedOption) }

    // Update internal state if the external selectedOption changes
    LaunchedEffect(selectedOption) {
        currentSelection = selectedOption
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = currentSelection.organizationType!!,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.organizationType!!) },
                    onClick = {
                        currentSelection = option
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}



