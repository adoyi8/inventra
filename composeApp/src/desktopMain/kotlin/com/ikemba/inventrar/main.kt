package com.ikemba.inventrar

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.ikemba.inventrar.app.App
import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
import com.ikemba.inventrar.di.initKoin
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.inventra_logo_colored
import org.jetbrains.compose.resources.painterResource


fun main() {
    initKoin()
    application{

        var barcodeBuffer by remember { mutableStateOf("") }
        val dashboardViewModel: MutableState<DashboardViewModel?> = remember { mutableStateOf(null) }


        Window(
            onCloseRequest = ::exitApplication,
            title = "Inventrar",
            state = WindowState(isMinimized = false, placement = WindowPlacement.Maximized),
                    icon = painterResource(Res.drawable.inventra_logo_colored),
            onKeyEvent = {

                if (it.type == KeyEventType.KeyUp) {
                    val key = it.key
                    if (key == Key.Enter) {
                        println("Scanned Barcode: $barcodeBuffer")
                 dashboardViewModel?.value?.addToCartFromScanner(barcodeBuffer)// Handle barcode logic
                        barcodeBuffer = ""
                    } else {
                        barcodeBuffer += key.nativeKeyCode.toChar()

                    }
                    true
                } else {
                    // let other handlers receive this event
                    false
                }
            }
        ) {


            App(dashboardViewModel = dashboardViewModel)
        }
    }
}

