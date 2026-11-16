package com.sundroid.kobokobo.auth


import com.sundroid.kobokobo.user.domain.User
import dev.gitlive.firebase.auth.FirebaseUser


fun FirebaseUser.toUser(): User{
    return User(firstname = this.displayName, contactPhone = this.phoneNumber, id = this.uid)
}