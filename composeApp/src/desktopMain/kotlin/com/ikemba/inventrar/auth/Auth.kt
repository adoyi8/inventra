package com.ikemba.inventrar.auth

import dev.gitlive.firebase.auth.FirebaseUser

actual class GoogleAuthUiProvider {
    actual suspend fun signIn(): FirebaseUser?{

        return null
    }
    actual suspend fun signOut(){

    }
}