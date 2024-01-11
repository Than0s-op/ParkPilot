package com.application.parkpilot

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
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
        val loginScrollView: ScrollView = findViewById(R.id.loginScrollView)
        val OTPScrollView:ScrollView = findViewById(R.id.OTPScrollView)

        loginScrollView.visibility = View.VISIBLE
        OTPScrollView.visibility = View.GONE

        buttonVerifyPhoneNumber.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val countryCodePicker:com.hbb20.CountryCodePicker = findViewById(R.id.countryCodePicker)
            val numberTextView: TextView = findViewById(R.id.numberTextView)

            val phoneNumber:String = countryCodePicker.selectedCountryCodeWithPlus + editTextPhoneNumber.text.toString()
            numberTextView.text = phoneNumber
            phoneAuth.startPhoneNumberVerification(phoneNumber)
        }

        val OTPPinView:PinView = findViewById(R.id.OTPPinView)

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

        val OTPVerificationButton:Button = findViewById(R.id.OTPVerificationButton)
        OTPVerificationButton.setOnClickListener {
           phoneAuth.verifyPhoneNumberWithCode(phoneAuth.storedVerificationId.value,OTPPinView.text.toString())
        }

        phoneAuth.storedTaskResult.observe(this){

            // Credential match (OTP is correct)
            if(phoneAuth.storedTaskResult.value != null){
                val intent = Intent(this,Home::class.java)
                intent.putExtra("result",phoneAuth.storedTaskResult.value)
                startActivity(intent)
            }
            else{
                OTPPinView.visibility = View.GONE
                loginScrollView.visibility = View.VISIBLE
            }
        }

    }
}