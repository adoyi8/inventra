package com.sundroid.kobokobo.core.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomIcon(
    imageVector: ImageVector,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    size: Dp = 24.dp
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun CustomIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        icon()
    }
}





