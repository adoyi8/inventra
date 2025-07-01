package com.ikemba.inventrar

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.ikemba.inventrar.app.App
import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
import com.ikemba.inventrar.di.initKoin
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.inventra_logo
import inventrar.composeapp.generated.resources.inventra_logo_colored
import org.jetbrains.compose.resources.painterResource



@OptIn(ExperimentalComposeUiApi::class) // For KeyEvent.utf16CodePoint (though we might not rely on it heavily for KeyUp)
fun main2() {
    initKoin()
    application {
        var barcodeBuffer by remember { mutableStateOf("") }
        val dashboardViewModelState: MutableState<DashboardViewModel?> = remember { mutableStateOf(null) }
        //val dashboardViewModel: MutableState<DashboardViewModel?> = remember { mutableStateOf(null) }

        // Initialize ViewModel (example - replace with your actual Koin/DI setup)
        LaunchedEffect(Unit) {
            if (dashboardViewModelState.value == null) {
              //  dashboardViewModelState.value = DashboardViewModel() // Or fetch from Koin
            }
        }

        Window(
            onCloseRequest = ::exitApplication,
            title = "Inventrar",
            state = WindowState(isMinimized = false, placement = WindowPlacement.Maximized),
            // icon = painterResource(Res.drawable.inventra_logo_colored), // Assuming Res is set up
            onKeyEvent = { keyEvent ->
                var consumed = false

                if (keyEvent.type == KeyEventType.KeyDown) {
                    // If a key is pressed down that could be part of a barcode,
                    // consume it immediately to prevent focused elements from acting on it.
                    // We won't add to the buffer on KeyDown if we are accumulating on KeyUp,
                    // but we must prevent default actions.
                    val charRepresentation = keyEventToChar(keyEvent) // Helper to get char
                    if (charRepresentation != null || keyEvent.key == Key.Enter) {
                        // If it's a character we'd append, or it's Enter, consume KeyDown.
                        consumed = true
                    }
                } else if (keyEvent.type == KeyEventType.KeyUp) {
                    val currentKey = keyEvent.key
                    if (currentKey == Key.Enter) {
                        if (barcodeBuffer.isNotEmpty()) {
                            println("Scanned Barcode (on Enter KeyUp): $barcodeBuffer")
                            dashboardViewModelState.value?.addToCartFromScanner(barcodeBuffer)
                            barcodeBuffer = "" // Clear buffer after processing
                        }
                        consumed = true // Consume the Enter KeyUp event
                    } else {
                        // Accumulate characters on KeyUp
                        val charToAdd = keyEventToChar(keyEvent)
                        if (charToAdd != null) {
                            barcodeBuffer += charToAdd
                            // For debugging:
                            // println("Appended: '$charToAdd', Buffer: '$barcodeBuffer', Key: ${keyEvent.key}")
                            consumed = true // Consume if we added a character
                        } else {
                            // If it wasn't a character we could map, but it was a KeyUp,
                            // and we previously consumed its KeyDown, we might still want to consume KeyUp.
                            // However, generally, if we didn't map it to a char, let it be.
                            // Unless it's a special non-char key from the scanner that we still need to suppress.
                            // This part depends on how "noisy" unmapped keys are.
                            // For now, only consume if a char was added.
                        }
                    }
                }
                // println("KeyEvent: ${keyEvent.key}, Type: ${keyEvent.type}, Consumed: $consumed, Buffer: '$barcodeBuffer'")
                consumed // Return true if we decided to consume this event
            }
        ) {
            App(dashboardViewModel = dashboardViewModelState)
        }
    }
}

/**
 * Helper function to convert a KeyEvent to a Char if it represents a
 * character typically found in barcodes.
 * This is more reliable than `key.nativeKeyCode.toChar()`.
 * It prioritizes actual character data if available (e.g. from `keyEvent.utf16CodePoint` on KeyDown)
 * or maps known `Key` enums.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun keyEventToChar(keyEvent: KeyEvent): Char? {
    // For KeyDown events, utf16CodePoint is often the best source for actual characters
    // if the system provides it (especially with various keyboard layouts/modifiers).
    // However, barcode scanners often send very "direct" key signals that map well to Key enums.
    if (keyEvent.type == KeyEventType.KeyDown && keyEvent.utf16CodePoint != 0) {
        val char = keyEvent.utf16CodePoint.toChar()
        // Filter out control characters, ensure it's something printable/expected
        if (char.isPrintableBarcodeChar()) {
            return char
        }
    }

    // Fallback or primary for KeyUp (where utf16CodePoint might be 0 or less reliable for this purpose)
    // Map specific Key enums to characters. Scanners usually don't involve complex Shift states
    // for basic digits/letters; they send the final character's key code.
    return when (keyEvent.key) {
        Key.Zero -> '0'
        Key.One,  -> '1'
        Key.Two -> '2'
        Key.Three -> '3'
        Key.Four, -> '4'
        Key.Five, -> '5'
        Key.Six -> '6'
        Key.Seven -> '7'
        Key.Eight ->  '8'
        Key.Nine -> '9'

        Key.A -> 'A'; Key.B -> 'B'; Key.C -> 'C'; Key.D -> 'D'; Key.E -> 'E'
        Key.F -> 'F'; Key.G -> 'G'; Key.H -> 'H'; Key.I -> 'I'; Key.J -> 'J'
        Key.K -> 'K'; Key.L -> 'L'; Key.M -> 'M'; Key.N -> 'N'; Key.O -> 'O'
        Key.P -> 'P'; Key.Q -> 'Q'; Key.R -> 'R'; Key.S -> 'S'; Key.T -> 'T'
        Key.U -> 'U'; Key.V -> 'V'; Key.W -> 'W'; Key.X -> 'X'; Key.Y -> 'Y'
        Key.Z -> 'Z'

        Key.Minus -> '-'
        Key.Period -> '.' // If your barcodes use periods
        Key.Spacebar -> ' ' // If your barcodes use spaces
        // Add other common barcode characters and their Key enum mappings as needed
        // e.g., Key.Slash, Key.Asterisk (if they have direct Key enum mappings)

        // Avoid `key.nativeKeyCode.toChar()` as a general fallback if possible,
        // unless you've confirmed it's necessary and reliable for specific unmapped keys
        // from your scanner.
        else -> null // Unhandled keys won't be added to the buffer
    }
}

/**
 * Helper to check if a character is generally printable and not a control character.
 * You might want to expand this if your barcodes have a very specific character set.
 */
fun Char.isPrintableBarcodeChar(): Boolean {
    return !this.isISOControl() && this != Char.MIN_VALUE // Basic filter for control/null chars
    // Add more specific checks if needed, e.g., Character.isLetterOrDigit(this) || this == '-' ...
}
fun main() {
     initKoin() // Your Koin initialization
    application() {
        var barcodeBuffer by remember { mutableStateOf("") }
        // val dashboardViewModelState: MutableState<DashboardViewModel?> = remember { mutableStateOf(DashboardViewModel()) } // Example instantiation
        // Ensure dashboardViewModel is correctly initialized, e.g., via remember { mutableStateOf(getKoin().get<DashboardViewModel>()) }
        // For this example, let's assume it's managed as in your original code:
        val dashboardViewModel: androidx.compose.runtime.MutableState<DashboardViewModel?> = remember { mutableStateOf(null) }


        Window(
            onCloseRequest = ::exitApplication,
            title = "Inventrar",
            icon = painterResource(Res.drawable.inventra_logo_colored),
            state = WindowState(isMinimized = false, placement = WindowPlacement.Maximized),
            // icon = painterResource(Res.drawable.inventra_logo_colored), // Assuming Res is generated by resources plugin
            onPreviewKeyEvent = { keyEvent -> // Changed from onKeyEvent to onPreviewKeyEvent
                if (keyEvent.type == KeyEventType.KeyUp) {
                    val currentKey = keyEvent.key // Get the Key enum value

                    if (currentKey == Key.Enter) {
                      //  if (barcodeBuffer.startsWith("xsxy")) {
                         //   val actualBarcode = barcodeBuffer.substring(4) // Extract content after "xsxy"
                          //  println("Scanned Barcode (Processed): $actualBarcode")
                            dashboardViewModel.value?.addToCartFromScanner(barcodeBuffer.trim())
                            barcodeBuffer = "" // Clear buffer after processing
                            true // Consume the Enter event fully, preventing focused item click
                      //  }
                    } else {
                        // Append character to buffer if it's not a control character.
                        // keyEvent.utf16CodePoint gives the actual character, respecting layout/modifiers.
                        val char = keyEvent.utf16CodePoint.toChar()

                        // Char.isISOControl() checks for U+0000-U+001F and U+007F-U+009F.
                        // This will correctly allow letters, digits, symbols, and space.
                        if (!char.isISOControl()) {
                            barcodeBuffer += char
                        }
                        // Always consume other key up events if we are potentially buffering a barcode.
                        // This prevents typed characters from also going into focused text fields elsewhere
                        // if this global handler is intended to capture all scanner-like input.
                        true
                    }
                } else {
                    // Not a KeyUp event (e.g., KeyDown).
                    // Return false to allow other handlers or default behavior to process it.
                    false
                }
            }
        ) {
             App(dashboardViewModel = dashboardViewModel) // Your main app UI
            // Placeholder App for example
            //androidx.compose.material.Text("Scan items using a USB HID keyboard scanner.\nOnly barcodes starting with 'xsxy' will be processed.\nLast scan attempt (buffer): $barcodeBuffer")
        }
    }
}
