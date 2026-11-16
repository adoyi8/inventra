package com.sundroid.kobokobo.core.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    elevation: CardElevation = CardDefaults.cardElevation(),
    shape: Shape = MaterialTheme.shapes.medium,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick ?: {},
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = elevation,
        shape = shape
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.padding(contentPadding)
        ) {
            content()
        }
    }
}





