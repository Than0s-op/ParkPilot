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
import com.application.parkpilot.module.firebase.authentication.GoogleSignIn
import com.application.parkpilot.module.firebase.authentication.PhoneAuth
import com.chaos.view.PinView
import com.hbb20.CountryCodePicker


class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authentication)

        // init views
        val editTextPhoneNumber: EditText = findViewById(R.id.editTextPhoneNumber)
        val buttonVerifyPhoneNumber: Button = findViewById(R.id.buttonVerifyPhoneNumber)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val scrollViewLogin: ScrollView = findViewById(R.id.scrollViewLogin)
        val scrollViewOTP: ScrollView = findViewById(R.id.scrollViewOTP)
        val pinViewOTP: PinView = findViewById(R.id.pinViewOTP)
        val buttonOTPVerification: Button = findViewById(R.id.buttonOTPVerification)
        val countryCodePicker: CountryCodePicker = findViewById(R.id.countryCodePicker)
        val textViewNumber: TextView = findViewById(R.id.textViewNumber)
        val buttonResendOTP: Button = findViewById(R.id.buttonResendOTP)
        var phoneNumberWithCountryCode = ""

//      ..........  phone auth  ................

        // creating the phoneAuth object
        val phoneAuth = PhoneAuth(this)

        // show login view to user and hide OTP view
        scrollViewLogin.visibility = View.VISIBLE
        scrollViewOTP.visibility = View.GONE


        buttonVerifyPhoneNumber.setOnClickListener {
            // show progress bar
            progressBar.visibility = View.VISIBLE

            // storing user number with country code
            phoneNumberWithCountryCode =
                countryCodePicker.selectedCountryCodeWithPlus + editTextPhoneNumber.text.toString()

            // set phone number with country code to OTP view's message (We have sent a verification code to)
            textViewNumber.text = "${countryCodePicker.selectedCountryCodeWithPlus} - ${editTextPhoneNumber.text}"

            // pass the phone number to phone auth manager
            phoneAuth.startPhoneNumberVerification(phoneNumberWithCountryCode)
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
                scrollViewLogin.visibility = View.GONE
                scrollViewOTP.visibility = View.VISIBLE

                // clear OTP box
                pinViewOTP.setText("")

                // show successful toast
                Toast.makeText(this, "OTP Send Successfully", Toast.LENGTH_SHORT).show()
            }
            // if OTP not send (error)
            else {
                // show failed toast
                Toast.makeText(this, "Failed to send OTP", Toast.LENGTH_SHORT).show()
            }
        }

        buttonOTPVerification.setOnClickListener {
            // pass user entered OTP and verification_ID to check entered OTP correct or not
            phoneAuth.verifyPhoneNumberWithCode(
                phoneAuth.storedVerificationId.value,
                pinViewOTP.text.toString()
            )
        }

        // phone auth Task observer. It will be react when user credential change
        phoneAuth.storedTaskResult.observe(this) {

            // Credential match (OTP is correct)
            if (phoneAuth.storedTaskResult.value != null) {
                // show successful toast
                Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()

                // just start the next activity
                startNextActivity()
            } else {
                // disable OTP view and show login view again
                scrollViewOTP.visibility = View.GONE
                scrollViewLogin.visibility = View.VISIBLE

                // clear phone number text view
                editTextPhoneNumber.setText("")

                // show failed toast
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }

        buttonResendOTP.setOnClickListener {
            phoneAuth.resendVerificationCode(phoneNumberWithCountryCode, phoneAuth.resendToken)
        }

//      ..........  google singIn  ................

        // initializing google sign in button
        val buttonGoogleSignIn: Button = findViewById(R.id.buttonGoogleLogin)

        // this will capture the google sign in auth activity result
        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                // if activity return result as "OK"
                if (result.resultCode == RESULT_OK) {
                    // just start the next activity
                    startNextActivity()
                } else {
                    // otherwise show toast "Failed to Login"
                    Toast.makeText(this, "Failed to Login", Toast.LENGTH_SHORT).show()
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
        val intent = Intent(this, MainActivity::class.java)
        // clear task stack
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        // start activity
        startActivity(intent)
    }
}