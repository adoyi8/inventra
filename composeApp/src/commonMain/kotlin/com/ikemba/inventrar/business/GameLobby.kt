package com.ikemba.inventrar.business

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikemba.inventrar.business.data.dto.SearchOrganizationRequest
import com.ikemba.inventrar.business.data.dto.SearchOrganizationResult
import com.ikemba.inventrar.login.presentation.UserViewModel


import kotlinx.coroutines.delay






val offWhite = Color(0xFFEAEAF2)











@Composable
fun RoomListContainer(userViewModel: UserViewModel) {
    val userState = userViewModel.userState.collectAsStateWithLifecycle()

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
        userViewModel.getBusinesses(SearchOrganizationRequest())
    }
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(delayMillis = 200)),
        exit = fadeOut()
    ) {


        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(offWhite.copy(alpha = 0.1f))
                .border(1.dp, offWhite.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                .padding(10.dp)

        ) {
            if(userState.value.business.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(
                        userState.value.business,
                        key = { _, room -> room.organizationId }) { index, room ->
                        var isItemVisible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) {
                            delay(100L * index) // Staggered delay
                            isItemVisible = true
                        }
                        AnimatedVisibility(
                            visible = isItemVisible,
                            enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                                initialOffsetX = { it / 2 }),
                            exit = fadeOut(animationSpec = tween(300))
                        ) {
                            val onClick ={
                                userViewModel.setSelectedBusiness(room)
                            }
                            RoomItem(room, onClick)
                        }
                    }
                }
            }
            else{
                Text("You are not attached to any business, you can create your own business and watch it grow")
            }
        }
    }
}

@Composable
fun AnimatedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = ButtonDefaults.shape,
    content: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f)

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            //.padding(16.dp)
            .size(width = 100.dp, height = 50.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .border(width = 2.dp, color = Color(0xFFfebaba))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF523f83), Color(0xFFffe6fb)) // blue to cyan
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(content, fontWeight = FontWeight.Bold)
    }
  //  content
}

@Composable
fun RoomItem(room: SearchOrganizationResult, onClick: () -> Unit) {


        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color.Red)) {

            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                    Text(room.organizationName, fontWeight = FontWeight.Bold)
                AnimatedButton(
                    onClick = onClick,
                    shape = RoundedCornerShape(8.dp),
                    content = "JOIN"
                    //  colors = ButtonDefaults.buttonColors(containerColor = Brush.),
                    //  contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                )



            }

        }}



