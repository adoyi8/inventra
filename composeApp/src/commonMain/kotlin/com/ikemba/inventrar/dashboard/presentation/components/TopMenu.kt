package com.ikemba.inventrar.dashboard.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikemba.inventrar.core.presentation.SecondaryColor
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.awt.SystemColor.menu


@Composable
fun TopMenu(viewModel: DashboardViewModel){
    val state = viewModel.state.collectAsStateWithLifecycle()

Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
    Spacer(Modifier.width(10.dp))
    state.value.menu.forEachIndexed { index, menu ->
        Box(
            modifier = Modifier
                .background(
                    color = if (state.value.selectedMenuIndex.currentState == index ) SecondaryColor else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(onClick = {
                    viewModel.onMenuSelected(index)
                })
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CustomText(
                    text = menu.title,
                    fontWeight = FontWeight.Bold,
                    color = if (state.value.selectedMenuIndex.currentState == index
                    ) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}
}