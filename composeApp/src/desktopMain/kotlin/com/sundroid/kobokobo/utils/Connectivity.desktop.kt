package com.sundroid.kobokobo.utils

import coil3.annotation.ExperimentalCoilApi
import coil3.network.ConnectivityChecker

@OptIn(ExperimentalCoilApi::class)
actual fun getConnectivityChecker(): ConnectivityChecker {
    return object : ConnectivityChecker {
        override fun isOnline(): Boolean {
            // A basic implementation for desktop that always assumes online, 
            // since desktop machines typically have stable internet 
            // and Coil will handle specific network errors if they occur.
            return true
        }
    }
}
