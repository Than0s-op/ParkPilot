package com.application.parkpilot.module.firebase.authentication

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit


// command: "./gradlew signingReport" to get SHA1 and SHA256
class PhoneAuth(private val context: Context) {

    // fire auth initialization
    private var auth: FirebaseAuth = Firebase.auth

    // live data to observe verificationID (OTP)
    val verificationId = MutableLiveData<String>()


    // It is useful to resend OTP
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    // to store call backs (onComplete, onFailed , onSend)
    private var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("PhoneAuthActivity", "onVerificationCompleted:$credential")
                // signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("PhoneAuthActivity", "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                    // reCAPTCHA verification attempted with null Activity
                }

                // setting empty to do some task (because it is a observer)
                this@PhoneAuth.verificationId.value = ""
            }

            override fun onCodeSent(
                verificationId: String, token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("PhoneAuthActivity", "onCodeSent:$verificationId")

                // assigning some value to do some task (because it is a observer)
                this@PhoneAuth.verificationId.value = verificationId

                // storing resendToken. It will need to resend OTP
                resendToken = token
            }
        }

    fun startPhoneNumberVerification(phoneNumber: String) {
        val options =
            PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phoneNumber) // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(context as Activity) // Activity (for callback binding)
                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    suspend fun verifyPhoneNumberWithCode(OTP: String): Boolean {
        // requesting for credential by using correct OTP and user entered OTP
        val credential = PhoneAuthProvider.getCredential(verificationId.value!!, OTP)

        // checking is got credential valid or not
        // if, it is Invalid user, it means user entered OTP is wrong
        return signInWithPhoneAuthCredential(credential)
    }

    fun resendVerificationCode(phoneNumber: String) {
        val optionsBuilder =
            PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phoneNumber) // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(context as Activity) // (optional) Activity for callback binding
                // If no activity is passed, reCAPTCHA verification can not be used.
                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                .setForceResendingToken(resendToken)

        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    private suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Boolean {
        var result: Boolean
//        auth.signInWithCredential(credential).addOnCompleteListener(context as Activity) { task ->
//            // entered OTP is correct
//            if (task.isSuccessful) {
//                // Sign in success, update UI with the signed-in user's information
//                Log.d("PhoneAuthActivity", "signInWithCredential:success")
//
//            } else {
//                // Sign in failed, display a message and update the UI
//                Log.w("PhoneAuthActivity", "signInWithCredential:failure", task.exception)
//
//                if (task.exception is FirebaseAuthInvalidCredentialsException) {
//                    result = false
//                }
//            }
//            result = task.isSuccessful
//        }
        try {
            result = auth.signInWithCredential(credential).await() != null
        } catch (e: Exception) {
            println("phone auth Exception: $e")
            result = false
        }
        return result
    }
}