package com.ikemba.inventrar.core.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressDialog(text: String) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Processing...") },
        confirmButton = {},
        text = {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.width(400.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(30.dp))
                Spacer(modifier = Modifier.width(8.dp))
                CustomText(text)
            }
        }

    )
}