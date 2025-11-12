package com.ikemba.inventrar
import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.google.firebase.FirebasePlatform

import com.ikemba.inventrar.auth.GoogleAuthUiProvider
import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
import com.ikemba.inventrar.di.initKoin
import com.ikemba.inventrar.login.presentation.UserViewModel
import com.ikemba.inventrar.splashScreen.presentation.SplashScreenRoot
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.inventra_logo_colored
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.context.startKoin

// Assuming these imports exist in your project structure:
// import com.ikemba.inventrar.navigation.SplashScreenRoot
// import com.ikemba.inventrar.login.presentation.GoogleAuthUiProvider
// import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
// import com.ikemba.inventrar.di.initKoin
// import com.ikemba.inventrar.common.Res

/**
 * Helper to check if a character is generally printable and not a control character.
 * This is used to filter out system key events not intended for the barcode buffer.
 */
fun Char.isPrintableBarcodeChar(): Boolean {
    // Basic filter for control/null chars
    return !this.isISOControl() && this != Char.MIN_VALUE
}

// NOTE: The `keyEventToChar` function was not used in the main logic,
// but is included here for completeness as it was present in the provided context.
@OptIn(ExperimentalComposeUiApi::class)
fun keyEventToChar(keyEvent: KeyEvent): Char? {
    // For KeyDown events, utf16CodePoint is often the best source for actual characters
    if (keyEvent.type == KeyEventType.KeyDown && keyEvent.utf16CodePoint != 0) {
        val char = keyEvent.utf16CodePoint.toChar()
        if (char.isPrintableBarcodeChar()) {
            return char
        }
    }

    // Fallback mapping for KeyUp (often used by Compose Desktop)
    return when (keyEvent.key) {
        Key.Zero -> '0'; Key.One -> '1'; Key.Two -> '2'; Key.Three -> '3'; Key.Four -> '4'
        Key.Five -> '5'; Key.Six -> '6'; Key.Seven -> '7'; Key.Eight -> '8'; Key.Nine -> '9'

        Key.A -> 'A'; Key.B -> 'B'; Key.C -> 'C'; Key.D -> 'D'; Key.E -> 'E'
        Key.F -> 'F'; Key.G -> 'G'; Key.H -> 'H'; Key.I -> 'I'; Key.J -> 'J'
        Key.K -> 'K'; Key.L -> 'L'; Key.M -> 'M'; Key.N -> 'N'; Key.O -> 'O'
        Key.P -> 'P'; Key.Q -> 'Q'; Key.R -> 'R'; Key.S -> 'S'; Key.T -> 'T'
        Key.U -> 'U'; Key.V -> 'V'; Key.W -> 'W'; Key.X -> 'X'; Key.Y -> 'Y'
        Key.Z -> 'Z'

        Key.Minus -> '-'; Key.Period -> '.'; Key.Spacebar -> ' '
        else -> null
    }
}


fun main() {
    // 1. **INITIALIZE FIREBASE FIRST**
    // Using the configuration for project 'seesay-32a33' and client 'com.ikemba.inventrar'.

    try {
        FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {
            val storage = mutableMapOf<String, String>()
            override fun store(key: String, value: String) = storage.set(key, value)
            override fun retrieve(key: String) = storage[key]
            override fun clear(key: String){storage.remove(key)}
            override fun log(msg: String) = println(msg) // Simple console log
        })
        println("FirebasePlatform initialized successfully.")
    } catch (e: Exception) {
        System.err.println("FirebasePlatform initialization failed: ${e.message}")
    }



    val options = FirebaseOptions(
        applicationId = "1:926103425935:android:b4815f674c978459cff1fa",
        apiKey = "AIzaSyAdQNX1IqxrqF1ub8aPB30_2lbkcf2MQ9E",
        projectId = "seesay-32a33",
        databaseUrl = "https://seesay-32a33.firebaseio.com",
        storageBucket = "seesay-32a33.appspot.com"
    )

    try {
        // THIS IS THE FIX:
        // Call initialize() on the GitLive 'Firebase' object, not the static 'FirebaseApp'
        Firebase.initialize (context = Application(), options = options)

        println("Firebase App Initialized Successfully.")
    } catch (e: Exception) {
        System.err.println("surulere Firebase initialization failed. If it's already running in the test environment, this is okay. Error: ${e.message}")
    }

    // 2. **START KOIN**
    // Assuming initKoin is defined in your di module setup
    initKoin()

    // 3. **START COMPOSE DESKTOP APPLICATION**
    @OptIn(ExperimentalComposeUiApi::class)
    application {
        var barcodeBuffer by remember { mutableStateOf("") }
        // DashboardViewModel is assumed to be a Koin component.
        // We use a mutableState to hold the instance, and initialize it later or rely on koinViewModel() inside the Window scope.
        val dashboardViewModel: androidx.compose.runtime.MutableState<DashboardViewModel?> = remember { mutableStateOf(null) }


        Window(
            onCloseRequest = ::exitApplication,
            title = "Inventrar",
            // NOTE: Replace 'Res.drawable.inventra_logo_colored' with a correct resource path if needed.
            icon = painterResource(Res.drawable.inventra_logo_colored),
            state = WindowState(isMinimized = false, placement = WindowPlacement.Maximized),
            // Use onPreviewKeyEvent to capture input globally, common for scanners
            onPreviewKeyEvent = { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyUp) {
                    val currentKey = keyEvent.key

                    if (currentKey == Key.Enter) {
                        // Process the buffer when Enter (EAN/barcode terminator) is pressed
                        dashboardViewModel.value?.addToCartFromScanner(barcodeBuffer.trim())
                        barcodeBuffer = "" // Clear buffer after processing
                        true // Consume the Enter event
                    } else {
                        // Append printable characters to buffer
                        val char = keyEvent.utf16CodePoint.toChar()

                        if (!char.isISOControl()) {
                            barcodeBuffer += char
                        }
                        // Consume the key event to stop it from affecting focused text fields
                        true
                    }
                } else {
                    false // Do not consume KeyDown or other events
                }
            }
        ) {
            // This assumes your components (SplashScreenRoot, GoogleAuthUiProvider) are defined elsewhere
            // koinViewModel() will resolve UserViewModel now that Firebase has been initialized and Koin is started.
            // Fix the koinViewModel() call for desktop
            SplashScreenRoot(
                googleAuthUiProvider = GoogleAuthUiProvider(),
                // 👇 ADD THIS IMPORT AND ARGUMENT:
                userViewModel = koinViewModel<UserViewModel>(extras = CreationExtras.Empty),
                businessViewModel = koinViewModel(extras = CreationExtras.Empty)
            )
        }
    }
}
