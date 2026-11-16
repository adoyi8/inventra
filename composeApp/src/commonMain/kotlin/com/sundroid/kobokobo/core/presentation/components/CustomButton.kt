package com.sundroid.kobokobo.core.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.sundroid.kobokobo.core.presentation.PrimaryColor

@Composable
fun CustomButton(
    onClick: () -> Unit = {},
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = PrimaryColor,
    contentColor: Color = Color.White,
    disabledBackgroundColor: Color = Color.Gray,
    disabledContentColor: Color = Color.White,
    border: BorderStroke? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    elevation: ButtonElevation = ButtonDefaults.elevatedButtonElevation()
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        border = border,
        shape = shape,
        contentPadding = contentPadding,
        elevation = elevation
    ) {
        CustomText(
            text = text,
            color = if (enabled) contentColor else disabledContentColor
        )
    }
}