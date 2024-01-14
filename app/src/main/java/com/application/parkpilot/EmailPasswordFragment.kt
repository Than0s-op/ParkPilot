package com.application.parkpilot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class EmailPasswordFragment : Fragment(R.layout.fragment_email_password) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val buttonPhoneLogin: Button = view.findViewById(R.id.buttonPhoneLogin)

        val editTextEmailID: EditText = view.findViewById(R.id.editTextEmailID)
        val editTextPassword:EditText = view.findViewById(R.id.editTextPassword)
        val buttonVerifyEmail:Button = view.findViewById(R.id.buttonVerifyEmail)
        val buttonForgetPassword: TextView = view.findViewById(R.id.buttonForgetPassword)
        val buttonRegister:TextView = view.findViewById(R.id.buttonRegister)

        val parentActivity = activity as AuthenticationActivity
        val emailPassword = EmailPassword(parentActivity)

        buttonRegister.setOnClickListener {

            // password length should be greater than 6
            emailPassword.createAccount(editTextEmailID.text.toString().trim(),editTextPassword.text.toString().trim())
        }

        buttonForgetPassword.setOnClickListener {
            emailPassword.resetPassword(editTextEmailID.text.toString().trim())
        }

        buttonVerifyEmail.setOnClickListener {
            emailPassword.signIn(editTextEmailID.text.toString().trim(),editTextPassword.text.toString().trim())
        }

        buttonPhoneLogin.setOnClickListener {
            parentActivity.supportFragmentManager.beginTransaction().apply {
                replace(R.id.frameLayout, PhoneAuthFragment())
                commit()
            }
        }
    }
}