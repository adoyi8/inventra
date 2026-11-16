package com.sundroid.kobokobo.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun CustomImage(
    painter: Painter,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    colorFilter: ColorFilter? = null,
    alpha: Float = 1f
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        colorFilter = colorFilter,
        alpha = alpha
    )
}

@Composable
fun CustomImage(
    resource: DrawableResource,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    colorFilter: ColorFilter? = null,
    alpha: Float = 1f
) {
    Image(
        painter = painterResource(resource),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        colorFilter = colorFilter,
        alpha = alpha
    )
}





