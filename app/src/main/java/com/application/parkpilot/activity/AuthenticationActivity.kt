package com.application.parkpilot.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.application.parkpilot.R
import com.application.parkpilot.module.GoogleSignIn
import com.application.parkpilot.module.PhoneAuth
import com.chaos.view.PinView


class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authentication)

        // init views
        val editTextPhoneNumber: EditText = findViewById(R.id.editTextPhoneNumber)
        val buttonVerifyPhoneNumber: Button = findViewById(R.id.buttonVerifyPhoneNumber)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val loginScrollView: ScrollView = findViewById(R.id.loginScrollView)
        val OTPScrollView: ScrollView = findViewById(R.id.OTPScrollView)
        val OTPPinView: PinView = findViewById(R.id.OTPPinView)
        val OTPVerificationButton: Button = findViewById(R.id.OTPVerificationButton)


//      ..........  phone auth  ................

        // creating the phoneAuth object
        val phoneAuth = PhoneAuth(this)

        // show login view to user and hide OTP view
        loginScrollView.visibility = View.VISIBLE
        OTPScrollView.visibility = View.GONE


        buttonVerifyPhoneNumber.setOnClickListener {
            // show progress bar
            progressBar.visibility = View.VISIBLE

            // creating objects of login view
            val countryCodePicker: com.hbb20.CountryCodePicker =
                findViewById(R.id.countryCodePicker)
            val numberTextView: TextView = findViewById(R.id.numberTextView)
            val phoneNumber: String =
                countryCodePicker.selectedCountryCodeWithPlus + editTextPhoneNumber.text.toString()

            // set phone number with country code to OTP view's message (We have sent a verification code to)
            numberTextView.text = phoneNumber

            // pass the phone number to phone auth manager
            phoneAuth.startPhoneNumberVerification(phoneNumber)
        }

        // phone auth verification Id observer, It will be react when verification Id changed
        phoneAuth.storedVerificationId.observe(this) {

            // If OTP send successfully or unsuccessfully, then hide progress bar
            progressBar.visibility = View.GONE

            // when "storedVerification == null" OTP send to failed,
            // otherwise OTP send successfully

            // if OTP send successfully
            if (phoneAuth.storedVerificationId.value != null) {
                // hide logIn view and show OTP view
                loginScrollView.visibility = View.GONE
                OTPScrollView.visibility = View.VISIBLE

                // clear OTP box
                OTPPinView.setText("")
            }
            // if OTP not send (error)
            else {

            }
        }

        OTPVerificationButton.setOnClickListener {
            // pass user entered OTP and verification_ID to check entered OTP correct or not
            phoneAuth.verifyPhoneNumberWithCode(
                phoneAuth.storedVerificationId.value,
                OTPPinView.text.toString()
            )
        }

        // phone auth Task observer. It will be react when user credential change
        phoneAuth.storedTaskResult.observe(this) {

            // Credential match (OTP is correct)
            if (phoneAuth.storedTaskResult.value != null) {
                // just start the next activity
                startNextActivity()
            } else {
                // disable OTP view and show login view again
                OTPScrollView.visibility = View.GONE
                loginScrollView.visibility = View.VISIBLE

                // clear phone number text view
                editTextPhoneNumber.setText("")
            }
        }


//      ..........  google singIn  ................

        val buttonGoogleSignIn: Button = findViewById(R.id.buttonGoogleLogin)

        // this will capture the google sign in auth activity result
        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                // if activity return result as "OK"
                if (result.resultCode == RESULT_OK) {
                    // just start the next activity
                    startNextActivity()
                }
                else{
                    // otherwise show toast "Invalid request"
                    Toast.makeText(this,"Invalid Request", Toast.LENGTH_SHORT).show()
                }
            }


        buttonGoogleSignIn.setOnClickListener {
            // init intent to google sign in activity
            val intent = Intent(this, GoogleSignIn::class.java)
            // launch the activity using above launcher
            resultLauncher.launch(intent)
        }
    }

    private fun startNextActivity() {
        // init intent to Main activity
        val intent = Intent(this, AuthenticationActivity::class.java)
        // clear task stack
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        // start activity
        startActivity(intent)
    }
}