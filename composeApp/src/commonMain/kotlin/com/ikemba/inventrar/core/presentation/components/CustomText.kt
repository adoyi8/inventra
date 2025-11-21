package com.ikemba.inventrar.core.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.montserrat
import org.jetbrains.compose.resources.Font

@Composable
fun CustomText(
    text: String,
    size: TextUnit = 16.sp,
    color: Color = Color.Unspecified,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    fontWeight: FontWeight = FontWeight.Normal,
    textDecoration: TextDecoration = TextDecoration.None,
    fontFamily: FontFamily? = FontFamily(
        Font(
            resource = Res.font.montserrat, // <-- Access the font via 'Res.font'
            weight = FontWeight.Normal
        )),
    fontStyle: FontStyle = FontStyle.Normal,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    lineHeight: TextUnit = TextUnit.Unspecified
) {
    Text(
        modifier = modifier,
        text = text,
        textAlign = textAlign,
        style = TextStyle(
            fontSize = size,
            color = color,
            fontWeight = fontWeight,
            textDecoration = textDecoration,
            fontFamily = fontFamily,
            fontStyle = fontStyle,
            lineHeight = lineHeight
        ),
        maxLines = maxLines,
        overflow = overflow
    )
}



