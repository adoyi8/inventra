package com.ikemba.inventrar.auth


import com.ikemba.inventrar.user.domain.User
import dev.gitlive.firebase.auth.FirebaseUser


fun FirebaseUser.toUser(): User{
    return User(firstname = this.displayName, contactPhone = this.phoneNumber, id = this.uid)
}