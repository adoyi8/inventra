package com.ikemba.inventrar.settings

import androidx.compose.foundation.isSystemInDarkTheme
import com.ikemba.inventrar.user.domain.User

data class SettingsState (
    val darkTheme: Boolean = false
)