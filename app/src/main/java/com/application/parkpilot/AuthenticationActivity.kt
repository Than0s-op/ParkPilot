package com.application.parkpilot

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.chaos.view.PinView
import com.google.firebase.auth.AuthResult


class AuthenticationActivity : AppCompatActivity() {

    lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authentication)

        progressBar = findViewById(R.id.progressBar)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, PhoneAuthFragment())
            commit()
        }
    }

    // below functions will call by fragments

    fun startNextActivity(result:AuthResult){
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("result", result)
        // clear task stack
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun googleSignInRequest(){

        //      ..........  google singIn  ................

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val obj = result.data?.getParcelableExtra<AuthResult>("result")
                    startNextActivity(obj!!)
                }
            }

        val buttonGoogleSignIn: Button = findViewById(R.id.buttonGoogleLogin)
        buttonGoogleSignIn.setOnClickListener {
            // start Google Sign In activity
            val intent = Intent(this, GoogleSignIn::class.java)
            resultLauncher.launch(intent)
        }
    }
}