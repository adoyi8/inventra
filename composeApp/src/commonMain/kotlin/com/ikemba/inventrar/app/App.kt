package com.ikemba.inventrar.app


import POScreen
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.ikemba.inventrar.core.presentation.NavigationViewModel
import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
import com.ikemba.inventrar.login.presentation.LoginAction
import com.ikemba.inventrar.login.presentation.LoginScreenRoot
import com.ikemba.inventrar.login.presentation.UserViewModel
import com.ikemba.inventrar.splashScreen.presentation.SplashScreenRoot
import com.ikemba.inventrar.splashScreen.presentation.SplashScreenViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import com.ikemba.inventrar.auth.GoogleAuthUiProvider
import com.ikemba.inventrar.dashboard.presentation.components.ConfirmLogout
import com.ikemba.inventrar.theme.AppTheme
import com.ikemba.inventrar.user.presentation.ShrineProfileApp

@Composable
@Preview
fun App(dashboardViewModel: MutableState<DashboardViewModel?>? = null, userViewModel: UserViewModel = koinViewModel<UserViewModel>(), googleAuthUiProvider: GoogleAuthUiProvider ) {
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
                startDestination = Route.SplashScreen
            ) {
                composable<Route.SplashScreen>(
                    exitTransition = { slideOutHorizontally() },
                    popEnterTransition = { slideInHorizontally() }
                ) {

                    val viewModel = koinViewModel<SplashScreenViewModel>()


                    SplashScreenRoot(
                        viewModel = viewModel,
                    )
                }
                composable<Route.POSScreen>(
                    exitTransition = { slideOutHorizontally() },
                    popEnterTransition = { slideInHorizontally() }
                ) {

                    if(dashboardViewModel!=null && dashboardViewModel.value ==null) {
                        dashboardViewModel?.value = koinViewModel()
                    }
                    POScreen(dashboardViewModel!!.value!!, userViewModel = userViewModel)
                }

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
                composable<Route.UserProfileRoute>(
                    exitTransition = { slideOutHorizontally() },
                    popEnterTransition = { slideInHorizontally() }
                ) {
                    ShrineProfileApp(userViewModel = userViewModel)
                }


            }

        }

    }
    ConfirmLogout(userViewModel)
}

@Composable
private inline fun <reified T: ViewModel> NavBackStackEntry.sharedKoinViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return koinViewModel<T>()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return koinViewModel(
        viewModelStoreOwner = parentEntry
    )
}