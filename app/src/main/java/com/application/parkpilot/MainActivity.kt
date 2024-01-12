package com.application.parkpilot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = Firebase.auth.currentUser
        if (user != null) {
            // User is signed in
            val intent = Intent(this,HomeActivity::class.java)
            intent.putExtra("result",user)
            startActivity(intent)
        } else {
            // No user is signed in
            startActivity(Intent(this,AuthenticationActivity::class.java))
        }
    }
}