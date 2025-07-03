package com.ikemba.inventrar

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
import com.ikemba.inventrar.app.App
import com.ikemba.inventrar.auth.GoogleAuthLauncher
import com.ikemba.inventrar.auth.GoogleAuthUiProvider
import com.ikemba.inventrar.dashboard.utils.Util

class MainActivity : ComponentActivity(), GoogleAuthLauncher {


    private lateinit var googleAuthUiProvider: GoogleAuthUiProvider
    private lateinit var googleSignInClient: GoogleSignInClient // Keep a reference if needed elsewhere (e.g., for signOut)

    // The ActivityResultLauncher
    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        println("ameen "+ result.data?.extras)
        googleAuthUiProvider.handleSignInIntentResult(result.data)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleAuthUiProvider = GoogleAuthUiProvider(this) // Pass 'this' activity context

        // Initialize your GMS GoogleSignInClient here too if you need it outside the provider
        // e.g., for signOut


        setContent {
            App(googleAuthUiProvider = googleAuthUiProvider)
        }
    }

    override fun launchGoogleSignIn(intent: Intent) {
        signInLauncher.launch(intent)
    }
}

//