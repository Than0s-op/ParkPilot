package com.application.parkpilot.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.application.parkpilot.CompanionObjects
import com.application.parkpilot.module.SharedPreference
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // getting current user status and set to the companion object "currentUser"
        CompanionObjects.currentUser = Firebase.auth.currentUser

        // User is signed in
        if (CompanionObjects.currentUser != null) {

            // if user is signIn but not filled the registration information
            // here we are checking, is user's UID exist in shared preferences
            // if result is null (no) otherwise (yes)
            if(SharedPreference(this).read(CompanionObjects.currentUser?.uid!!) != null){
                // throw user to the authentication activity
                startActivity(Intent(this, UserRegisterActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }

            else {
                // creating the home activity intent
                val intent = Intent(this, HomeActivity::class.java)

                // clear the activity stack
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
            }
        }
        // No user is signed in
        else {
            // through user to the authentication activity
            startActivity(Intent(this, AuthenticationActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }
}