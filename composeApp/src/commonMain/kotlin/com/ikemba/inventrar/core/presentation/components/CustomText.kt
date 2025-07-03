package com.ikemba.inventrar.core.presentation.components

import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun CustomText(text: String, size: TextUnit = 16.sp, color: Color = MaterialTheme.colorScheme.onBackground, modifier: Modifier = Modifier, textAlign: TextAlign = TextAlign.Start, fontWeight: FontWeight = FontWeight.Normal, textDecoration: TextDecoration = TextDecoration.None){


//    val fontFamily = FontFamily(
//        Font(
//            resource = "fonts/montserrat.ttf",
//            weight = FontWeight.W400,
//            style = FontStyle.Normal
//        )
//    )

    Text(modifier = modifier, text = text, textAlign = textAlign, style = TextStyle(fontSize = size,color = color, fontWeight = fontWeight, textDecoration = textDecoration,
       // fontFamily = fontFamily
    ))
}