package com.sundroid.kobokobo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.sundroid.kobokobo.app.App
import com.sundroid.kobokobo.auth.GoogleAuthLauncher
import com.sundroid.kobokobo.auth.GoogleAuthUiProvider
import com.sundroid.kobokobo.dashboard.utils.Util
import com.sundroid.kobokobo.splashScreen.presentation.SplashScreenRoot
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity(), GoogleAuthLauncher {


    private lateinit var googleAuthUiProvider: GoogleAuthUiProvider
    private lateinit var googleSignInClient: GoogleSignInClient // Keep a reference if needed elsewhere (e.g., for signOut)

    // The ActivityResultLauncher
    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        googleAuthUiProvider.handleSignInIntentResult(result.data)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleAuthUiProvider = GoogleAuthUiProvider(this) // Pass 'this' activity context

        // Initialize your GMS GoogleSignInClient here too if you need it outside the provider
        // e.g., for signOut


        setContent {
            SplashScreenRoot(googleAuthUiProvider = googleAuthUiProvider, userViewModel = koinViewModel())
        }
    }

    override fun launchGoogleSignIn(intent: Intent) {
        signInLauncher.launch(intent)
    }
}

//