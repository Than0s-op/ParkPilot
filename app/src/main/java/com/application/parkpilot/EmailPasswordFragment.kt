package com.application.parkpilot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class EmailPasswordFragment : Fragment(R.layout.fragment_email_password) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val buttonPhoneLogin: Button = view.findViewById(R.id.buttonPhoneLogin)

        val parentActivity = activity as AuthenticationActivity
        buttonPhoneLogin.setOnClickListener {
            parentActivity.supportFragmentManager.beginTransaction().apply {
                replace(R.id.frameLayout, PhoneAuthFragment())
                commit()
            }
        }
    }
}