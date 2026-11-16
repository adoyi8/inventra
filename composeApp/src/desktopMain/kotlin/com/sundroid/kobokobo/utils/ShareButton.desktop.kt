package com.sundroid.kobokobo.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

@Composable
actual fun ShareAppButton(textToShare: String) {
    IconButton(onClick = {
        // Desktop doesn't have a direct "share sheet" like Android.
        // Copying to clipboard as a fallback.
        val selection = StringSelection(textToShare)
        Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
    }) {
        Icon(Icons.Default.Share, contentDescription = "Copy to Clipboard")
    }
}
