package com.application.parkpilot

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import kotlin.properties.Delegates

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        FirebaseApp.initializeApp(this)
    }
}
object User{
    // type of users
    const val ANONYMOUS = 0
    const val FINDER = 1

    // if UID is null it means user not login yet
    lateinit var UID:String

    // It will store type of user
    var type by Delegates.notNull<Int>()
}