package com.sundroid.kobokobo.utils

import coil3.network.ConnectivityChecker


// Define the expected function to get the platform-specific implementation
expect fun getConnectivityChecker(): ConnectivityChecker