package com.application.parkpilot.viewModel

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.parkpilot.User
import com.application.parkpilot.module.PhotoLoader
import com.application.parkpilot.module.firebase.Storage
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    fun loadProfileImage(context: Context, profileImage: MenuItem) {
        viewModelScope.launch {
            Storage().userProfilePhotoGet(User.UID)?.let { uri ->
                profileImage.icon = PhotoLoader().getImage(context, uri).drawable
            }
        }
    }
}