package com.application.parkpilot.viewModel

import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.parkpilot.module.PermissionRequest
import com.application.parkpilot.module.firebase.database.Feedback
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.application.parkpilot.StationLocation as DS_StationLocation
import com.application.parkpilot.module.firebase.database.StationLocation as FS_StationLocation

class SpotListViewModel : ViewModel() {
    val liveDataStationLocation = MutableLiveData<ArrayList<DS_StationLocation>?>()
    val permission = PermissionRequest()
    fun loadStation() {
        viewModelScope.launch {
            liveDataStationLocation.value = FS_StationLocation().locationGet()
        }
    }

    fun sortByDistance(context: Context, list: ArrayList<DS_StationLocation>) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        viewModelScope.launch {
            try {
                val currentLocation = fusedLocationClient.lastLocation.await()
                liveDataStationLocation.value = ArrayList(list.sortedBy {
                    currentLocation.distanceTo(Location("").apply {
                        latitude = it.coordinates.latitude
                        longitude = it.coordinates.longitude
                    })
                })
            } catch (_: Exception) {
            }
        }
    }

    fun sortByRating(list: ArrayList<DS_StationLocation>) {
        viewModelScope.launch {
            val feedback = Feedback()
            val ratings = HashMap<String, Float>()
            for (station in list) {
                var rating = 0.0f
                val stationFeedbacks = feedback.feedGet(station.stationUid!!)
                for (map in stationFeedbacks) {
                    rating += map.value.rating
                }
                if (stationFeedbacks.isNotEmpty())
                    ratings[station.stationUid] = rating / stationFeedbacks.size
                else
                    ratings[station.stationUid] = 0.0f
            }
            liveDataStationLocation.value = ArrayList(list.sortedByDescending {
                ratings[it.stationUid]
            })
        }
    }
}