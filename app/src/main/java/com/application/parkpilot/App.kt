package com.application.parkpilot

import android.app.Application
import com.google.firebase.FirebaseApp
import kotlin.properties.Delegates

class App : Application() {
    override fun onCreate() {
        super.onCreate()
//        DynamicColors.applyToActivitiesIfAvailable(this)
        // To start firebase analytics ( It may be remove reCAPTCHA)
        FirebaseApp.initializeApp(this)
    }
}
object User{
    // type of users
    val ANONYMOUS = 0
    val FINDER = 1
    val OWNER = 2

    // if UID is null it means user not login yet
    lateinit var UID:String

    // It will store type of user
    var type by Delegates.notNull<Int>()
}