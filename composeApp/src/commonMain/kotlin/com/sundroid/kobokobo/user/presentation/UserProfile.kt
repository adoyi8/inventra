package com.sundroid.kobokobo.user.presentation

/*
 * App.kt
 * This file contains the entire application logic for the Shrine-inspired profile page,
 * updated to use Material 3 components.
 */

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
// Material 3 imports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.sundroid.kobokobo.core.presentation.components.CustomText
import com.sundroid.kobokobo.core.presentation.components.ProgressDialog
import com.sundroid.kobokobo.dashboard.presentation.components.BlurredImageBackground

import com.sundroid.kobokobo.login.presentation.UserViewModel


@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        BlurredImageBackground(
            imageUrl = FirebaseAuth.getInstance().currentUser?.photoUrl.toString(),
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomText(FirebaseAuth.getInstance().currentUser?.displayName?:"", style = 20.sp, fontWeight = FontWeight.Bold)
        CustomText(FirebaseAuth.getInstance().currentUser?.email?:"", color = Color.Gray)
        CustomText(FirebaseAuth.getInstance().currentUser?.uid?:"", color = Color.Gray)
        CustomText(FirebaseAuth.getInstance().currentUser?.phoneNumber?:"", color = Color.Gray)
    }
}
