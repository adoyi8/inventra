package com.sundroid.kobokobo.core.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    CircularProgressIndicator(
        modifier = modifier,
        color = color,
        strokeWidth = strokeWidth,
        trackColor = trackColor
    )
}

@Composable
fun CustomLoadingScreen(
    modifier: Modifier = Modifier,
    message: String = "Loading...",
    color: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            CustomProgressIndicator(color = color)
            CustomText(text = message, color = color)
        }
    }
}





