package com.sundroid.kobokobo.app

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.sundroid.kobokobo.admin.presentation.AdminViewModel
import com.sundroid.kobokobo.auth.GoogleAuthUiProvider
import com.sundroid.kobokobo.business.presentation.BusinessListViewModel
import com.sundroid.kobokobo.business.presentation.BusinessViewModel
import com.sundroid.kobokobo.core.presentation.NavigationViewModel
import com.sundroid.kobokobo.dashboard.presentation.DashboardViewModel
import com.sundroid.kobokobo.dashboard.presentation.components.ConfirmLogout
import com.sundroid.kobokobo.dropdowndata.presentation.DropDownSettingsViewModel
import com.sundroid.kobokobo.login.presentation.LoginAction
import com.sundroid.kobokobo.login.presentation.LoginScreenRoot
import com.sundroid.kobokobo.login.presentation.UserViewModel
import com.sundroid.kobokobo.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun AuthNavGraph(dashboardViewModel: MutableState<DashboardViewModel?>? = null, userViewModel: UserViewModel, businessViewModel: BusinessViewModel = koinViewModel(), dropDownSettingsViewModel: DropDownSettingsViewModel = koinViewModel(), adminViewModel: AdminViewModel = koinViewModel(), googleAuthUiProvider: GoogleAuthUiProvider, businessListViewModel: BusinessListViewModel = koinViewModel()  ) {
    AppTheme {
        val navController = rememberNavController()

        LaunchedEffect(Unit) {
            NavigationViewModel.navController=navController
            userViewModel.googleAuthUIProvider = googleAuthUiProvider
        }

        NavHost(
            navController = navController,
            startDestination = Route.BookGraph
        ) {

            navigation<Route.BookGraph>(
                startDestination = Route.Login
            ) {
                composable<Route.Login>(
                    exitTransition = { slideOutHorizontally() },
                    popEnterTransition = { slideInHorizontally() }
                ) {

                    LaunchedEffect(true) {
                        val  action = LoginAction.OnToggleVisibility(true)
                        userViewModel.onAction(action)
                    }
                    LoginScreenRoot(
                        viewModel = userViewModel, googleAuthProvider = googleAuthUiProvider
                    )
                }




            }

        }

    }
    ConfirmLogout(userViewModel)
}
