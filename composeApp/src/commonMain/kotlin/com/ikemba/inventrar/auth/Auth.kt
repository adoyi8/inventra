package com.ikemba.inventrar.auth

// composeApp/src/commonMain/kotlin/Auth.kt

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow

expect class GoogleAuthUiProvider {
    suspend fun signIn(): FirebaseUser?
    suspend fun signOut()
}

object Auth {
    private val auth = Firebase.auth

    fun getcurrentUser(): FirebaseUser? = auth.currentUser

    fun onAuthStateChanged(): Flow<FirebaseUser?> = auth.authStateChanged
}