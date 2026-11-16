package com.sundroid.kobokobo.utils


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import coil3.annotation.ExperimentalCoilApi
import coil3.network.ConnectivityChecker

// Get the Application context (you must ensure you provide this somewhere,
// e.g., via a dependency injection framework or a top-level property)
lateinit var applicationContext: Context

@OptIn(ExperimentalCoilApi::class)
actual fun getConnectivityChecker(): ConnectivityChecker {
    return object : ConnectivityChecker {
        override fun isOnline(): Boolean {
            val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            // Check for modern Android versions (API 23+)
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }
    }
}
