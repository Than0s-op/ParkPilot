package com.application.parkpilot.viewModel

import android.content.Context
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.parkpilot.R
import com.application.parkpilot.User
import com.application.parkpilot.activity.Authentication
import com.application.parkpilot.activity.Main
import com.application.parkpilot.activity.UserRegister
import com.application.parkpilot.module.PhotoLoader
import com.application.parkpilot.module.firebase.Storage
import com.application.parkpilot.module.firebase.database.UserBasic
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class SettingViewModel : ViewModel() {
    fun login(context: Context) {
        context.startActivity(Intent(context, Authentication::class.java))
    }

    fun logout(context: Context) {
        // sign out the user
        Firebase.auth.signOut()
        Toast.makeText(context, "Logout Successfully", Toast.LENGTH_SHORT).show()

        // creating the intent of Authentication activity
        val intent = Intent(context, Main::class.java).apply {
            // to clear activity stack
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // start the activity
        context.startActivity(intent)
    }

    fun personalInformation(context: Context) {
        context.startActivity(Intent(context, UserRegister::class.java))
    }

    fun loadProfile(
        context: Context,
        profileImage: ImageView,
        profileName: TextView,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            Firebase.auth.currentUser?.let {
                val image = Storage().userProfilePhotoGet(User.UID)
                val name = UserBasic().getProfile(User.UID)?.userName
                image?.let {
                    profileImage.setImageDrawable(
                        PhotoLoader().getImage(
                            context,
                            it,
                            false
                        ).drawable
                    )
                }
                name?.let {
                    profileName.text = it
                }
            }
            onComplete()
        }
    }
}