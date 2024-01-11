package com.application.parkpilot

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.chaos.view.PinView

class Authentication: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authentication)
        val editTextPhoneNumber:EditText = findViewById(R.id.editTextPhoneNumber)
        val buttonVerifyPhoneNumber: Button = findViewById(R.id.buttonVerifyPhoneNumber)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val phoneAuth = PhoneAuthActivity(this)

        buttonVerifyPhoneNumber.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val countryCodePicker:com.hbb20.CountryCodePicker = findViewById(R.id.countryCodePicker)
            phoneAuth.startPhoneNumberVerification(countryCodePicker.selectedCountryCodeWithPlus + editTextPhoneNumber.text.toString())
        }

        val OTPPinView:PinView = findViewById(R.id.OTPPinView)

        phoneAuth.storedVerificationId.observe(this, Observer {
            //Do something with the changed value -> it
            progressBar.visibility = View.GONE

            val loginScrollView: ScrollView = findViewById(R.id.loginScrollView)
            val OTPScrollView:ScrollView = findViewById(R.id.OTPScrollView)

            loginScrollView.visibility = View.GONE
            OTPScrollView.visibility = View.VISIBLE
            OTPPinView.text = null
        })

        val OTPVerivicationButton:Button = findViewById(R.id.OTPVerificationButton)
        OTPVerivicationButton.setOnClickListener {
           phoneAuth.verifyPhoneNumberWithCode(phoneAuth.storedVerificationId.value,OTPPinView.toString())
        }
    }
}