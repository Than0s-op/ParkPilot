package com.application.parkpilot.viewModel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.parkpilot.activity.Authentication
import com.application.parkpilot.activity.Main
import com.application.parkpilot.module.firebase.authentication.GoogleSignIn
import com.application.parkpilot.module.firebase.authentication.PhoneAuth
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class AuthenticationViewModel(context: Context) : ViewModel() {


//      .......... [ phone auth ] ................


    // phone auth init
    val phoneAuth by lazy { PhoneAuth(context) }

    // it will store result of code check
    val verifyPhoneNumberWithCodeResult = MutableLiveData<Boolean>()

    // it will store result of google signIn
    val googleSignInResult = MutableLiveData<Boolean>()

    // it will store state of login scroll view visibility [ To handel reconfiguration]
    var scrollViewLoginVisibility = View.VISIBLE

    // it will store state of OTP scroll view visibility [ To handel reconfiguration]
    var scrollViewOTPVisibility = View.GONE

    // it will store mobile number of user [ To handel reconfiguration]
    var phoneNumberWithCountryCode = ""

    fun sendVerificationCode() {
        // pass the phone number to phone auth manager
        phoneAuth.startPhoneNumberVerification(phoneNumberWithCountryCode)
    }

    fun verifyPhoneNumberWithCode(otp: String) {
        viewModelScope.launch {
            // store result of verification code. It will be true (if code match) or false
            verifyPhoneNumberWithCodeResult.value =
                phoneAuth.verifyPhoneNumberWithCode(otp)
        }
    }

    fun resendVerificationCode() {
        // request to phone auth to resend verification code
        phoneAuth.resendVerificationCode(phoneNumberWithCountryCode)
    }


//      .......... [ google singIn ] ................


    // this will capture the google sign in auth activity result
    private val resultLauncher =
        (context as AppCompatActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // store true or false
            googleSignInResult.value = result.resultCode == AppCompatActivity.RESULT_OK
        }


    fun startGoogleSignInIntent(context: Context) {
        // init intent to google sign in activity
        val intent = Intent(context, GoogleSignIn::class.java)

        // launch the activity using above launcher
        resultLauncher.launch(intent)
    }


//      .......... [ other contain ] ................

    fun startNextActivity(context: Context) {
        // init intent to Main activity
        val intent = Intent(context, Main::class.java)
        // clear task stack
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        // start activity
        context.startActivity(intent)
    }

    fun dashSeparate(number: String): String {
        if (number.length != 13) return number

        // it will just add '-'. like this +91-1234567890
        return number.substring(0, 3) + "-" + number.substring(3)
    }
}