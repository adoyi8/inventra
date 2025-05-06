package com.ikemba.inventrar.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CoreErrorScreen(visibleState: MutableTransitionState<Boolean>, errorMessage: String) {

    AnimatedVisibility(visibleState = visibleState) {
        AlertDialog(
            onDismissRequest = {visibleState.targetState = false},
            title = { Text("Info!") },
            confirmButton = {
                CustomButton(
                    {visibleState.targetState = false},
                    text = "Ok" )
                            },

            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.width(400.dp)
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    CustomText(errorMessage)
                }
            }

        )
    }
}