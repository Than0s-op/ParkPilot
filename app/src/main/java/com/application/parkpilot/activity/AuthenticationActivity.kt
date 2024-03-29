package com.application.parkpilot.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.application.parkpilot.R
import com.application.parkpilot.viewModel.AuthenticationViewModel
import com.chaos.view.PinView
import com.hbb20.CountryCodePicker


class AuthenticationActivity : AppCompatActivity(R.layout.authentication) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        // getting authentication view model reference [init]
        val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthenticationViewModel(this@AuthenticationActivity) as T
            }
        })[AuthenticationViewModel::class.java]

        // [init]
        // setting phone number to (We have sent a verification code to) text view
        // [we are setting here because of reconfiguration, if activity will regenerate we will set phone number to that view again]
        textViewNumber.text = viewModel.dashSeparate(viewModel.phoneNumberWithCountryCode)

//      .......... [ phone auth ] ................


        // setting visibility as according to view model [init]
        scrollViewLogin.visibility = viewModel.scrollViewLoginVisibility
        scrollViewOTP.visibility = viewModel.scrollViewOTPVisibility


        buttonVerifyPhoneNumber.setOnClickListener { _ ->
            // show progress bar
            progressBar.visibility = View.VISIBLE

            // storing user number with country code
            viewModel.phoneNumberWithCountryCode =
                countryCodePicker.selectedCountryCodeWithPlus + editTextPhoneNumber.text.toString()

            // set phone number with country code to OTP view's message (We have sent a verification code to)
            textViewNumber.text = viewModel.dashSeparate(viewModel.phoneNumberWithCountryCode)

            // start verification
            viewModel.sendVerificationCode()
        }

        // view model verification Id observer, It will be react when verification Id will change
        viewModel.verificationCode.observe(this) {
            it.getContentIfNotHandled()?.let { verificationCode ->

                // If OTP send successfully or unsuccessfully, then hide progress bar
                progressBar.visibility = View.GONE

                // when "verificationId == null" OTP send to failed,
                // otherwise OTP send successfully

                // if OTP send successfully
                if (verificationCode.isNotEmpty()) {
                    // hide login view and
                    View.GONE.let {
                        scrollViewLogin.visibility = it
                        viewModel.scrollViewLoginVisibility = it
                    }

                    // show OTP view
                    View.VISIBLE.let {
                        scrollViewOTP.visibility = it
                        viewModel.scrollViewOTPVisibility = it
                    }

                    // show successful toast
                    Toast.makeText(this, "OTP Send Successfully", Toast.LENGTH_SHORT).show()
                }
                // if OTP not send successfully (error)
                else {
                    // show failed toast
                    Toast.makeText(this, "Failed to send OTP", Toast.LENGTH_SHORT).show()
                }
            }
        }

        buttonOTPVerification.setOnClickListener { _ ->
            // pass user entered OTP to check entered OTP correct or not
            viewModel.verifyPhoneNumberWithCode(
                pinViewOTP.text.toString()
            )
        }

        // It will be react when we get result of the "verifyPhoneNumberWithCode" function call
        viewModel.verifyPhoneNumberWithCodeResult.observe(this) {
            it.getContentIfNotHandled()?.let { isCorrect ->
                // when Credential match (OTP is correct)
                if (isCorrect) {
                    // show successful toast
                    Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()

                    // start the next activity
                    viewModel.startNextActivity(this)
                } else {
                    // clear OTP box
                    pinViewOTP.setText("")

                    // disable OTP view and
                    View.GONE.let {
                        scrollViewOTP.visibility = it
                        viewModel.scrollViewOTPVisibility = it
                    }

                    // show login view again
                    View.VISIBLE.let {
                        scrollViewLogin.visibility = it
                        viewModel.scrollViewLoginVisibility = it
                    }

                    // clear phone number text view
                    editTextPhoneNumber.setText("")

                    // show failed toast
                    Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
                }
            }
        }

        buttonResendOTP.setOnClickListener { _ ->
            // resend verification code request
            viewModel.resendVerificationCode()
        }

//      .......... [ google singIn ] ................

        // initializing google sign in button
        val buttonGoogleSignIn: Button = findViewById(R.id.buttonGoogleLogin)

        buttonGoogleSignIn.setOnClickListener { _ ->
            // start the google sign in intent
            viewModel.startGoogleSignInIntent(this)
        }

        viewModel.googleSignInResult.observe(this) {
            it.getContentIfNotHandled()?.let { isOk ->
                // successfully get google account of user
                if (isOk) {
                    viewModel.startNextActivity(this)
                }
                // otherwise
                else {
                    Toast.makeText(this, "Failed to Login", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}