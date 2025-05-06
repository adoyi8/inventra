package com.ikemba.inventrar

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform