package com.ikemba.inventrar.core.presentation.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ikemba.inventrar.core.presentation.PrimaryColor


@Composable
fun CustomButton(onClick:()-> Unit= {}, text: String, modifier: Modifier = Modifier){
    return Button(modifier = modifier, onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)){
        CustomText(text, color = Color.White)
    }
}