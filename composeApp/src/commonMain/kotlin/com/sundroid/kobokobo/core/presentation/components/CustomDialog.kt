package com.sundroid.kobokobo.core.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomAlertDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    confirmButton: @Composable (() -> Unit)? = null,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    titleContentColor: Color = MaterialTheme.colorScheme.onSurface,
    textContentColor: Color = MaterialTheme.colorScheme.onSurface,
    iconContentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        title = title,
        text = text,
        confirmButton = {confirmButton},
        dismissButton = dismissButton,
        icon = icon,
        shape = shape,
        containerColor = containerColor,
        titleContentColor = titleContentColor,
        textContentColor = textContentColor,
        iconContentColor = iconContentColor
    )
}

@Composable
fun CustomTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        text()
    }
}





