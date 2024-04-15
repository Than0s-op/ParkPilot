package com.application.parkpilot.viewModel.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Location
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.parkpilot.activity.SpotDetail
import com.application.parkpilot.module.firebase.Storage
import com.application.parkpilot.module.firebase.database.Feedback
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.application.parkpilot.StationBasic as DC_StationBasic
import com.application.parkpilot.module.firebase.database.StationAdvance as FS_StationAdvance
import com.application.parkpilot.module.firebase.database.StationBasic as FS_StationBasic

class SpotList : ViewModel() {
    private val storage = Storage()
    private val stationBasic = FS_StationBasic()
    private val stationAdvance = FS_StationAdvance()
    private val feedback = Feedback()
    val liveDataStationBasic = MutableLiveData<DC_StationBasic>()
    val liveDataImages = MutableLiveData<List<Uri>>()
    val liveDataFeedback = MutableLiveData<Pair<Float, Int>>()
    val liveDataAmenities = MutableLiveData<List<String>>()
    val liveDataDistance = MutableLiveData<String>()

    fun getDistance(client: FusedLocationProviderClient, geoPoint: GeoPoint) {
        viewModelScope.launch {
            try {
                val currentLocation = client.lastLocation.await()
                liveDataDistance.value =
                    String.format("%.1f", currentLocation.distanceTo(Location("").apply {
                        longitude = geoPoint.longitude
                        latitude = geoPoint.latitude
                    }) / 1000) + "km"
            } catch (_: Exception) {
            }
        }
    }

    fun loadBasic(stationUID: String) {
        viewModelScope.launch {
            val stationBasic = stationBasic.basicGet(stationUID)
            stationBasic?.apply {
                liveDataStationBasic.value = DC_StationBasic(name, price, rating)
            }
        }
    }

    fun loadAmenities(stationUID: String) {
        viewModelScope.launch {
            val stationAdvance = stationAdvance.advanceGet(stationUID)
            stationAdvance?.let {
                liveDataAmenities.value = it.amenities
            }
        }
    }

    fun loadRating(stationUID: String) {
        viewModelScope.launch {
            val feedbacks = feedback.feedGet(stationUID)
            var totalRatting = 0.0f
            for (i in feedbacks) {
                totalRatting += i.value.rating
            }
            liveDataFeedback.value = Pair(totalRatting, feedbacks.size)
        }
    }

    fun loadImages(stationUID: String) {
        viewModelScope.launch {
            liveDataImages.value = storage.parkSpotPhotoGet(stationUID)
        }
    }

    fun getTint(ratting: Float): ColorStateList {
        return if (ratting <= 2.5) {
            ColorStateList.valueOf(Color.parseColor("#e5391a"))
        } else if (ratting < 4) {
            ColorStateList.valueOf(Color.parseColor("#cb8300"))
        } else {
            ColorStateList.valueOf(Color.parseColor("#026a28"))
        }
    }

    fun startNextActivity(context: Context, stationUID: String) {
        val intent = Intent(context, SpotDetail::class.java).apply {
            putExtra("stationUID", stationUID)
        }
        context.startActivity(intent)
    }
}