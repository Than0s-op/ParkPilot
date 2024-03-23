package com.application.parkpilot.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.application.parkpilot.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.application.parkpilot.User as appUser

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // getting current user status

        // if User is signed in below block will execute (if current user is not null)
        if (Firebase.auth.currentUser?.uid != null) {

            /*
                if user is signIn but not filled the registration information
                here we are checking, is user's UID exist in shared preferences (local storage)
                if result is null (no) otherwise (yes)
                NOTE : [ above logic will prove wrong, when user login though another mobile, because we can't find user's UID on that device ]
            */

            // store UID in application layer
            appUser.UID = Firebase.auth.currentUser?.uid!!

            // setting user type as "Finder"
            User.type = User.FINDER
            nextActivity()
        }

        // No user is signed in yet (anonymous)
        else {
            User.type = User.ANONYMOUS
            // through user to the Home activity
            nextActivity()
        }
    }

    private fun nextActivity() {
        // start the home activity
        startActivity(Intent(
            this@MainActivity,
            HomeActivity::class.java
        ).apply {
            // clear the activity stack
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}