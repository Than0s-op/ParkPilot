package com.application.parkpilot.module

import android.content.Context
import android.content.SharedPreferences

class SharedPreference(context:Context) {
    // creating share preference using "package name" (it will be unique) and mode is private (no any other app can read our app's data)
    private val sharedPreference = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    // write function to write shared preferences it will take key (as UID) and information
    fun write(key: String, information: String) {
        // shared preference editor to edit/create shared preference
        val editor: SharedPreferences.Editor = sharedPreference.edit()

        // creating shared preference
        editor.putString(key,information)

        // apply changes (this is async function, it won't block UI thread)
        editor.apply()
    }

    fun read(key: String): String? {
        // if is not preset return "null" otherwise return the result
        return sharedPreference.getString(key,null)
    }
}