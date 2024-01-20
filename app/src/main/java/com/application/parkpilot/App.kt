package com.application.parkpilot

import android.app.Application
import com.google.android.material.color.DynamicColors
import com.google.firebase.FirebaseApp

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        FirebaseApp.initializeApp(this)
    }
}