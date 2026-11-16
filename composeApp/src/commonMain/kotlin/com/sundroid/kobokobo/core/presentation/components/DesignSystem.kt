package com.sundroid.kobokobo.core.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.sundroid.kobokobo.core.presentation.PrimaryColor
import com.sundroid.kobokobo.core.presentation.SecondaryColor

// Button Variants
@Composable
fun PrimaryButton(
    onClick: () -> Unit = {},
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    CustomButton(
        onClick = onClick,
        text = text,
        modifier = modifier,
        enabled = enabled,
        backgroundColor = PrimaryColor,
        contentColor = Color.White
    )
}

@Composable
fun SecondaryButton(
    onClick: () -> Unit = {},
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    CustomButton(
        onClick = onClick,
        text = text,
        modifier = modifier,
        enabled = enabled,
        backgroundColor = SecondaryColor,
        contentColor = PrimaryColor
    )
}

@Composable
fun OutlinedPrimaryButton(
    onClick: () -> Unit = {},
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = PrimaryColor
        ),
        border = BorderStroke(1.dp, PrimaryColor)
    ) {
        CustomText(text = text, color = PrimaryColor)
    }
}

// Text Variants
@Composable
fun HeadingText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground,
    textAlign: TextAlign = TextAlign.Start,
    size: TextUnit = TextUnit.Unspecified
) {
    CustomText(
        text = text,
        style = size,
        color = color,
        modifier = modifier,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
        textAlign = textAlign
    )
}

@Composable
fun SubheadingText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    CustomText(
        text = text,
        style = androidx.compose.ui.unit.TextUnit.Unspecified,
        color = color,
        modifier = modifier,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
    )
}

@Composable
fun BodyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground,
    textAlign: TextAlign,
    size: TextUnit
) {
    CustomText(
        text = text,
        color = color,
        modifier = modifier,
        style = size,
        textAlign = textAlign
    )
}

@Composable
fun CaptionText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textAlign: TextAlign = TextAlign.Center,
    size: TextUnit = TextUnit.Unspecified
) {
    CustomText(
        text = text,
        style = size,
        textAlign = textAlign,
        color = color,
        modifier = modifier
    )
}





