package com.ikemba.inventrar.app

import POScreen
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.ikemba.inventrar.admin.TwoStepMenuScreen
import com.ikemba.inventrar.admin.presentation.AdminViewModel
import com.ikemba.inventrar.admin.presentation.CreateInventoryForm
import com.ikemba.inventrar.auth.GoogleAuthUiProvider
import com.ikemba.inventrar.business.presentation.BusinessListViewModel
import com.ikemba.inventrar.business.presentation.BusinessViewModel
import com.ikemba.inventrar.core.presentation.NavigationViewModel
import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
import com.ikemba.inventrar.dashboard.presentation.components.ConfirmLogout
import com.ikemba.inventrar.dropdowndata.presentation.DropDownSettingsViewModel
import com.ikemba.inventrar.login.presentation.LoginAction
import com.ikemba.inventrar.login.presentation.LoginScreenRoot
import com.ikemba.inventrar.login.presentation.UserViewModel
import com.ikemba.inventrar.splashScreen.presentation.SplashScreenRoot
import com.ikemba.inventrar.splashScreen.presentation.SplashScreenViewModel
import com.ikemba.inventrar.theme.AppTheme
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
