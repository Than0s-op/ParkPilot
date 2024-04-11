package com.application.parkpilot.viewModel

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.parkpilot.ParkPilotMapLegend
import com.application.parkpilot.User
import com.application.parkpilot.activity.BookingHistoryActivity
import com.application.parkpilot.activity.HomeActivity
import com.application.parkpilot.activity.ProfileActivity
import com.application.parkpilot.bottomSheet.SpotPreview
import com.application.parkpilot.module.OSM
import com.application.parkpilot.module.PhotoLoader
import com.application.parkpilot.module.firebase.Storage
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import com.application.parkpilot.module.firebase.database.StationLocation as FireStoreStationLocation

class HomeViewModel : ViewModel() {

    fun loadProfileImage(context: Context, profileImage: MenuItem) {
        viewModelScope.launch {
            Storage().userProfilePhotoGet(User.UID)?.let { uri ->
                profileImage.icon = PhotoLoader().getImage(context, uri).drawable
            }
        }
    }

    fun profile(context: Context) {
        context.startActivity(Intent(context, ProfileActivity::class.java))
    }

    fun history(context: Context) {
        context.startActivity(Intent(context, BookingHistoryActivity::class.java))
    }
}