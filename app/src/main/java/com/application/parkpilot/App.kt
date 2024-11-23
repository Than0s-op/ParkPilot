package com.application.parkpilot

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.application.parkpilot.module.PermissionRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await
import kotlin.properties.Delegates

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        FirebaseApp.initializeApp(this)
    }
}

object User {
    // type of users
    const val ANONYMOUS = 0
    const val FINDER = 1

    // if UID is null it means user not login yet
    lateinit var UID: String

    // It will store type of user
    var type by Delegates.notNull<Int>()
}

object Utils {
    fun isLocalUri(uri: Uri): Boolean {
        return uri.scheme == "file" || uri.scheme == "content" || uri.scheme == "android.resource"
    }

    fun errorToast(context: Context, message: String) {
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_LONG
        val inflate =
            (context as AppCompatActivity).layoutInflater.inflate(R.layout.toast, null).apply {
                findViewById<LinearLayout>(R.id.linearLayout).backgroundTintList =
                    ColorStateList.valueOf(Color.RED)
                findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.block_icon)
                findViewById<TextView>(R.id.textView).text = message
            }
        toast.view = inflate
        toast.show()
    }

    fun truthToast(context: Context, message: String) {
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_LONG
        val inflate =
            (context as AppCompatActivity).layoutInflater.inflate(R.layout.toast, null).apply {
                findViewById<LinearLayout>(R.id.linearLayout).backgroundTintList =
                    ColorStateList.valueOf(Color.GREEN)
                findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.check_circle_icon)
                findViewById<TextView>(R.id.textView).text = message
            }
        toast.view = inflate
        toast.show()
    }

    fun startActivityWithCleanStack(context: Context, intent: Intent) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    suspend fun getLastKnowLocation(context: Context): GeoPoint? {
        // permission class object creation
        val activity = PermissionRequest()

        // current location set to null (if permission is not granted, we can't enter in "if" block)
        var currentLocation: GeoPoint? = null

        // check is location permission granted or not. If Not request to user for location permission
        if (activity.locationPermissionRequest(context)) {
            // request to turn on location(GPS)
            activity.gpsPermissionRequest(context)

            // below code will give last know location of user. But It will "null" if user's GPS is turned off
            LocationServices.getFusedLocationProviderClient(context).lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        currentLocation = GeoPoint(location.latitude, location.longitude)
                    }
                }.addOnFailureListener {
                    println("location service: $it")
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }.await()
        }
        return currentLocation
    }
}