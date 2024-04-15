package com.application.parkpilot.viewModel

import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.parkpilot.activity.Feedback
import com.application.parkpilot.module.firebase.Storage
import com.application.parkpilot.module.firebase.database.StationAdvance
import com.application.parkpilot.module.firebase.database.StationBasic
import com.application.parkpilot.module.firebase.database.StationLocation
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.application.parkpilot.StationAdvance as StationAdvanceDataClass
import com.application.parkpilot.StationBasic as StationBasicDataClass
import com.application.parkpilot.module.firebase.database.Feedback as FS_Feedback

class SpotPreviewViewModel : ViewModel() {

    val carouselImages = MutableLiveData<List<Any>>()
    val stationBasicInfo = MutableLiveData<StationBasicDataClass>()
    val stationAdvanceInfo = MutableLiveData<StationAdvanceDataClass>()
    val stationRating = MutableLiveData<Pair<Float, Int>>()
    val liveDataDistance = MutableLiveData<String>()
    private var stationLocation: GeoPoint? = null
    private var currentLocation: Location? = null

    fun loadCarousel(stationUID: String) {
        viewModelScope.launch {
            carouselImages.value = Storage().parkSpotPhotoGet(stationUID)
        }
    }

    fun loadBasicInfo(stationUID: String) {
        viewModelScope.launch {
            stationBasicInfo.value = StationBasic().basicGet(stationUID)
        }
    }

    fun loadAdvanceInfo(stationUID: String) {
        viewModelScope.launch {
            stationAdvanceInfo.value = StationAdvance().advanceGet(stationUID)
        }
    }

    fun getDistance(context: Context, stationUID: String) {
        viewModelScope.launch {
            stationLocation = StationLocation().locationGet(stationUID)
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                currentLocation = fusedLocationClient.lastLocation.await()
                stationLocation?.let {station->
                    currentLocation?.let {current->
                        liveDataDistance.value =
                            String.format("%.1f", current.distanceTo(Location("").apply {
                                longitude = station.longitude
                                latitude = station.latitude
                            }) / 1000) + "km"
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun redirect(context: Context) {
        stationLocation?.let { station ->
            currentLocation?.let { current ->
                val uri =
                    Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${current.latitude},${current.longitude}&destination=${station.latitude},${station.longitude}")

                // Create an Intent to open Google Maps with the specified URI
                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(mapIntent)
            }
        }
    }

    fun loadRating(stationUID: String) {
        viewModelScope.launch {
            val feedbacks = FS_Feedback().feedGet(stationUID)
            var totalRatting = 0.0f
            for (i in feedbacks) {
                totalRatting += i.value.rating
            }
            stationRating.value = Pair(totalRatting, feedbacks.size)
        }
    }

    fun feedback(context: Context, stationUid: String) {
        context.startActivity(Intent(context, Feedback::class.java).apply {
            putExtra("stationUID", stationUid)
        })
    }
}