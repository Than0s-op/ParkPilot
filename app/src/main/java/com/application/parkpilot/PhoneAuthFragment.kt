package com.application.parkpilot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import com.chaos.view.PinView

class PhoneAuthFragment : Fragment(R.layout.fragment_phone_auth) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val editTextPhoneNumber: EditText = view.findViewById(R.id.editTextPhoneNumber)
        val buttonVerifyPhoneNumber: Button = view.findViewById(R.id.buttonVerifyPhoneNumber)
        val buttonEmailLogin: Button = view.findViewById(R.id.buttonEmailLogin)
        val loginScrollView: ScrollView = view.findViewById(R.id.loginScrollView)
        val OTPScrollView: ScrollView = view.findViewById(R.id.OTPScrollView)
        val OTPPinView: PinView = view.findViewById(R.id.OTPPinView)
        val OTPVerificationButton: Button = view.findViewById(R.id.OTPVerificationButton)

        // type cast object in AuthenticationActivity
        val parentActivity = (activity as AuthenticationActivity)
        val phoneAuth = PhoneAuth(parentActivity)

        // Init google SignIn Request
//        parentActivity.googleSignInRequest()

        loginScrollView.visibility = View.VISIBLE
        OTPScrollView.visibility = View.GONE

        buttonVerifyPhoneNumber.setOnClickListener {
            parentActivity.progressBar.visibility = View.VISIBLE

            val countryCodePicker: com.hbb20.CountryCodePicker = view.findViewById(R.id.countryCodePicker)
            val numberTextView: TextView = view.findViewById(R.id.numberTextView)
            val phoneNumber: String =
                countryCodePicker.selectedCountryCodeWithPlus + editTextPhoneNumber.text.toString()
            numberTextView.text = phoneNumber

            phoneAuth.startPhoneNumberVerification(phoneNumber)
        }


        phoneAuth.storedVerificationId.observe(viewLifecycleOwner) {
            //Do something with the changed value -> it
            parentActivity.progressBar.visibility = View.GONE

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

        phoneAuth.storedTaskResult.observe(viewLifecycleOwner) {

            // Credential match (OTP is correct)
            if (phoneAuth.storedTaskResult.value != null) {
                // just start the next activity
                parentActivity.startNextActivity(phoneAuth.storedTaskResult.value!!)
            } else {
                OTPScrollView.visibility = View.GONE
                loginScrollView.visibility = View.VISIBLE
            }
        }

        buttonEmailLogin.setOnClickListener {
            parentActivity.supportFragmentManager.beginTransaction().apply {
                replace(R.id.frameLayout, EmailPasswordFragment())
                commit()
            }
        }
    }
}