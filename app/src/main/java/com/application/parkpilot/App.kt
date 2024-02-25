package com.application.parkpilot

import android.app.Application
import com.google.firebase.FirebaseApp

class App : Application() {
    override fun onCreate() {
        super.onCreate()
//        DynamicColors.applyToActivitiesIfAvailable(this)
        // To start firebase analytics ( It may be remove reCAPTCHA)
        FirebaseApp.initializeApp(this)
    }
}
object User{
    // if UID is null it means user not login yet
    lateinit var UID:String
}