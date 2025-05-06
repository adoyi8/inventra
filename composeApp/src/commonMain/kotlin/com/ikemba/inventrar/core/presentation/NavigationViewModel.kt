package com.ikemba.inventrar.core.presentation

import androidx.navigation.NavHostController

object NavigationViewModel{
    var navController: NavHostController? = null


    fun navigate(route: String) {
        navController?.navigate(route) // Ensure it’s initialized before navigating
    }
}