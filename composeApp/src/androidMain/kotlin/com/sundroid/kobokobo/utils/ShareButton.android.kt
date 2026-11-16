package com.sundroid.kobokobo.utils

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun ShareAppButton(textToShare: String) {
    val context = LocalContext.current
    IconButton(onClick = {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, textToShare)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Inventra")
        context.startActivity(shareIntent)
    }) {
        Icon(Icons.Default.Share, contentDescription = "Share App")
    }
}
