package com.ikemba.inventrar.app

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object BookGraph: Route

    @Serializable
    data object BookList: Route

    @Serializable
    data object Login: Route

    @Serializable
    data object SplashScreen: Route

    @Serializable
    data object POSScreen: Route

    @Serializable
    data object UserProfileRoute: Route

    @Serializable
    data class BookDetail(val id: String): Route
}