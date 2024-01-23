package com.application.parkpilot

import com.google.firebase.auth.FirebaseUser

class CompanionObjects {
    companion object{
        // this will be store current user status
        var currentUser: FirebaseUser? = null
    }
}