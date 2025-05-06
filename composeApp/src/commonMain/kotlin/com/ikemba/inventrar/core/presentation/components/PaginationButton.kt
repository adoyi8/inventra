package com.ikemba.inventrar.core.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.ikemba.inventrar.core.presentation.PrimaryColor









@Composable
fun PaginationButton(onClick:()-> Unit= {}, text: String, enabled: Boolean, modifier: Modifier = Modifier){
    return Button(modifier = modifier, onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor, disabledContainerColor = Color.White, disabledContentColor = Color.Gray, contentColor = Color.White), enabled = enabled, border = BorderStroke(1.dp, Color.Gray), shape = RectangleShape){
        Text(text)
    }
}