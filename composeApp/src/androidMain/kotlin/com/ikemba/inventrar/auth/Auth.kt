package com.ikemba.inventrar.auth

import android.content.Context
import android.content.Intent
import androidx.credentials.GetCredentialRequest

import androidx.credentials.CredentialManager
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
//import com.google.android.gms.common.api.ApiException
//import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.ikemba.inventrar.dashboard.utils.Util
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException // Assuming R file is accessible or put WEB_CLIENT_ID directly

// An interface to be implemented by your Activity/Fragment that needs to launch the intent
// This remains the same as it's the bridge to the Android UI layer
interface GoogleAuthLauncher {
    fun launchGoogleSignIn(intent: Intent)
}

actual class GoogleAuthUiProvider(private val context: Context) {

    // This will hold the continuation to resume the signIn suspend function
    private var signInContinuation: ((GoogleSignInAccount?, Exception?) -> Unit)? = null

    // This is the function your Activity/Fragment will call to deliver the result
    fun handleSignInIntentResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(Exception::class.java)
            signInContinuation?.invoke(account, null)
        } catch (e: Exception) {
            println("Jummai 2 "+ e.message)
            signInContinuation?.invoke(null, e)
        } catch (e: Exception) {
            println("Jummai 3 "+ e.message)
            signInContinuation?.invoke(null, e)
        } finally {
            signInContinuation = null // Clear the continuation after use
        }
    }

    actual suspend fun signIn(): FirebaseUser? {

  //      signInWithGoogle(context)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // Use your web client ID from strings.xml or directly
            // Make sure you have a google-services.json file in your project
            // and the Google Services Gradle plugin applied.
            .requestIdToken(Util.REQUEST_TOKEN_ID)
           //  .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)

        return suspendCancellableCoroutine { continuation ->
            // Store the continuation so handleSignInIntentResult can resume it
            signInContinuation = { account, exception ->

                println("awari ")
                if (continuation.isActive) {
                    if (account != null) {
                        println("awari 2")
                        continuation.resume(account)
                    } else if (exception != null) {
                        println("awari 3 "+ exception.message)
                        continuation.resumeWithException(exception)
                    } else {
                        println("awari 4")
                        // Should not happen, but for safety
                        continuation.resumeWithException(IllegalStateException("Google Sign-In failed with no account or exception."))
                    }
                }
            }

            // Launch the intent.
            if (context is GoogleAuthLauncher) {
                context.launchGoogleSignIn(googleSignInClient.signInIntent)
            } else {
                continuation.resumeWithException(IllegalStateException("Context provided to GoogleAuthUiProvider must implement GoogleAuthLauncher."))
            }
        }.let { account ->
            try {
                // Use GoogleAuthProvider from dev.gitlive and Firebase.auth from dev.gitlive
                val credential = GoogleAuthProvider.credential(account.idToken!!, null)
                Firebase.auth.signInWithCredential(credential).user
            } catch (e: Exception) {
                println("Jummai "+ e.message)
                e.printStackTrace()
                null
            }
        }
        return null
    }
    actual suspend fun signOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignInClient.signOut().await() // Use await for GMS Task
        Firebase.auth.signOut() // Sign out from GitLive Firebase Auth
    }
}
private suspend fun firebaseAuthWithGoogle(googleIdCredential: GoogleIdTokenCredential) : FirebaseUser?{
    val auth = Firebase.auth
    var user: FirebaseUser? = null
    try {
        // 5. Use the token to create a GitLive Firebase credential
        val credential = GoogleAuthProvider.credential(
            idToken = googleIdCredential.idToken,
            accessToken = null // Access token is not needed for this flow
        )

        // 6. Sign in to Firebase with the GitLive library
        val authResult = auth.signInWithCredential(credential)
         user = authResult.user
        println("Jummai: Firebase sign-in successful! User: ${user?.displayName}")
        // --- Navigate to your main screen or update UI ---

    } catch (e: Exception) {
        println("Jummai 3: Firebase authentication failed: ${e.message}")
        // Handle Firebase authentication errors
    }
    return user;
}