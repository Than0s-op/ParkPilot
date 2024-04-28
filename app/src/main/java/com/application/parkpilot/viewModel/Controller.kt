package com.application.parkpilot.viewModel

import android.content.Context
import android.view.MenuItem
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.parkpilot.User
import com.application.parkpilot.fragment.BookingHistory
import com.application.parkpilot.fragment.Map
import com.application.parkpilot.fragment.Setting
import com.application.parkpilot.fragment.SpotList
import com.application.parkpilot.module.PhotoLoader
import com.application.parkpilot.module.firebase.Storage
import kotlinx.coroutines.launch

class Controller : ViewModel() {
    val spotList by lazy { SpotList() }
    val mapFragment by lazy { Map() }
    val bookingHistory by lazy { BookingHistory() }
    val setting by lazy { Setting() }
    fun loadProfileImage(context: Context, profileImage: MenuItem) {
        viewModelScope.launch {
            Storage().userProfilePhotoGet(User.UID)?.let { uri ->
                profileImage.icon = PhotoLoader().getImage(context, uri).drawable
            }
        }
    }
}