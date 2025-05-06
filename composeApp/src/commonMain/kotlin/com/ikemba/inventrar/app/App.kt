package com.ikemba.inventrar.app


import POScreen
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.MaterialTheme
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
import com.ikemba.inventrar.login.presentation.LoginViewModel
import com.ikemba.inventrar.splashScreen.presentation.SplashScreenRoot
import com.ikemba.inventrar.splashScreen.presentation.SplashScreenViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(dashboardViewModel: MutableState<DashboardViewModel?>? = null) {
    MaterialTheme {
        val navController = rememberNavController()



        LaunchedEffect(Unit) {
            NavigationViewModel.navController=navController
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
                    POScreen(dashboardViewModel!!.value!!)
                }

                composable<Route.Login>(
                    exitTransition = { slideOutHorizontally() },
                    popEnterTransition = { slideInHorizontally() }
                ) {
                    val viewModel = koinViewModel<LoginViewModel>()
                    LaunchedEffect(true) {
                       val  action = LoginAction.OnToggleVisibility(true)
                        viewModel.onAction(action)
                    }
                    LoginScreenRoot(
                        viewModel = viewModel
                    )
                }


            }

        }

    }
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