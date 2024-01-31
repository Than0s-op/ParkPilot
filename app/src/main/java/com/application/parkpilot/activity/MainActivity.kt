package com.application.parkpilot.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.application.parkpilot.module.firebase.FireStore
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // getting current user status
        val user = Firebase.auth.currentUser

        // if User is signed in below block will execute (if current user is not null)
        if (user != null) {

            /*
                if user is signIn but not filled the registration information
                here we are checking, is user's UID exist in shared preferences (local storage)
                if result is null (no) otherwise (yes)
                NOTE : [ above logic will prove wrong, when user login though another mobile, because we can't find user's UID on that device ]
            */

            // FireStore's method are suspended that why we are using coroutine
            CoroutineScope(Dispatchers.Default).launch {

                // getting data of user from fireStore

                // if user's data has present below block will execute
                if (FireStore().userGet(user.uid) != null) {
                    // start the home activity
                    startActivity(Intent(
                        this@MainActivity,
                        HomeActivity::class.java
                    ).apply {
                        // clear the activity stack
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }

                // otherwise, user has not registered yet
                else {
                    // start the registration activity
                    startActivity(Intent(
                        this@MainActivity,
                        UserRegisterActivity::class.java
                    ).apply {
                        // clear the activity stack
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }
            }
        }

        // No user is signed in yet
        else {
            // through user to the authentication activity
            startActivity(Intent(
                this,
                AuthenticationActivity::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }
}