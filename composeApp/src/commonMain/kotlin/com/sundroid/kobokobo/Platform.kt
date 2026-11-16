package com.sundroid.kobokobo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform