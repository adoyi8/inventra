package com.sundroid.kobokobo.settings

import androidx.compose.foundation.isSystemInDarkTheme
import com.sundroid.kobokobo.user.domain.User

data class SettingsState (
    val darkTheme: Boolean = false
)