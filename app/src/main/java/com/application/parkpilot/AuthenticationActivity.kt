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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authentication)

        val editTextPhoneNumber: EditText = findViewById(R.id.editTextPhoneNumber)
        val buttonVerifyPhoneNumber: Button = findViewById(R.id.buttonVerifyPhoneNumber)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val loginScrollView: ScrollView = findViewById(R.id.loginScrollView)
        val OTPScrollView: ScrollView = findViewById(R.id.OTPScrollView)
        val OTPPinView: PinView = findViewById(R.id.OTPPinView)
        val OTPVerificationButton: Button = findViewById(R.id.OTPVerificationButton)


//      ..........  phone auth  ................

        val phoneAuth = PhoneAuth(this)

        loginScrollView.visibility = View.VISIBLE
        OTPScrollView.visibility = View.GONE

        buttonVerifyPhoneNumber.setOnClickListener {
            progressBar.visibility = View.VISIBLE

            val countryCodePicker: com.hbb20.CountryCodePicker = findViewById(R.id.countryCodePicker)
            val numberTextView: TextView = findViewById(R.id.numberTextView)
            val phoneNumber: String =
                countryCodePicker.selectedCountryCodeWithPlus + editTextPhoneNumber.text.toString()
            numberTextView.text = phoneNumber

            phoneAuth.startPhoneNumberVerification(phoneNumber)
        }


        phoneAuth.storedVerificationId.observe(this) {
            //Do something with the changed value -> it
            progressBar.visibility = View.GONE

            // if OTP send successfully
            if (phoneAuth.storedVerificationId.value != null) {
                loginScrollView.visibility = View.GONE
                OTPScrollView.visibility = View.VISIBLE
                OTPPinView.text = null
            }
            // if OTP not send (error)
            else {

            }
        }

        OTPVerificationButton.setOnClickListener {
            phoneAuth.verifyPhoneNumberWithCode(
                phoneAuth.storedVerificationId.value,
                OTPPinView.text.toString()
            )
        }

        phoneAuth.storedTaskResult.observe(this) {

            // Credential match (OTP is correct)
            if (phoneAuth.storedTaskResult.value != null) {
                // just start the next activity
                startNextActivity(phoneAuth.storedTaskResult.value!!)
            } else {
                OTPScrollView.visibility = View.GONE
                loginScrollView.visibility = View.VISIBLE
            }
        }


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
            val intent = Intent(this, GoogleSignIn::class.java)
            resultLauncher.launch(intent)
        }
    }

    private fun startNextActivity(result:AuthResult){
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("result", result)
        // clear task stack
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}