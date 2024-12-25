package com.application.parkpilot.viewModel

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.parkpilot.FreeSpotDetails
import com.application.parkpilot.SpotListCardDetails
import com.application.parkpilot.Utils
import com.application.parkpilot.module.PermissionRequest
import com.application.parkpilot.module.firebase.Storage
import com.application.parkpilot.module.firebase.database.Feedback
import com.application.parkpilot.module.firebase.database.FreeSpot
import com.application.parkpilot.module.firebase.database.StationAdvance
import com.application.parkpilot.module.firebase.database.StationBasic
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import com.application.parkpilot.StationLocation as DS_StationLocation
import com.application.parkpilot.module.firebase.database.StationLocation as FS_StationLocation

class SpotListViewModel : ViewModel() {
    private val storage = Storage()
    private val stationBasic = StationBasic()
    private val stationAdvance = StationAdvance()
    private val feedback = Feedback()
    private val location = FS_StationLocation()
    private val freeSpot = FreeSpot()
    var stationList = emptyList<SpotListCardDetails>()
    val liveDataStationList = MutableLiveData<List<SpotListCardDetails>>()

    fun loadStations(context: Context) {
        viewModelScope.launch {
            val currentLocation = Utils.getLastKnowLocation(context)
            val result = mutableListOf<SpotListCardDetails>()
            val paidSpotList = location.locationGet()
            for (i in paidSpotList) {
                result.add(
                    getSpotListCardDetails(
                        documentId = i.stationUid!!,
                        location = i.coordinates,
                        currentLocation = currentLocation,
                    ).copy(isFree = false)
                )
            }
            val freeSpotList = freeSpot.getLocations()
            for (i in freeSpotList) {
                result.add(
                    getFreeSpotListCardDetails(
                        documentId = i.stationUid!!,
                        location = i.coordinates,
                        currentLocation = currentLocation
                    )
                )
            }
            stationList = result
            liveDataStationList.value = result
        }
    }

    fun sortByDistance() {
        liveDataStationList.value = liveDataStationList.value?.sortedBy { it.distance }
    }

    fun sortByRating() {
        liveDataStationList.value = liveDataStationList.value?.sortedByDescending {
            val rating = it.rating?.first ?: Float.NEGATIVE_INFINITY
            val numberOfUsers = if(it.rating?.second == 0) 1 else it.rating?.second ?: 1
            return@sortedByDescending rating / numberOfUsers
        }
    }

    fun applyFilter(list: List<Boolean>) {
        liveDataStationList.value =
            stationList.filter { if (it.isFree) list[0] else list[1] }
    }

    private suspend fun getSpotListCardDetails(
        documentId: String,
        location: GeoPoint,
        currentLocation: GeoPoint?
    ): SpotListCardDetails {
        val stationBasic = stationBasic.basicGet(documentId)
        val stationAdvance = stationAdvance.advanceGet(documentId)
        val feedbacks = feedback.feedGet(documentId)
        var result = SpotListCardDetails(documentId = documentId)

        stationBasic?.apply {
            result = result.copy(
                name = name ?: "",
                price = price
            )
        }

        stationAdvance?.let {
            result = result.copy(
                amenities = it.amenities
            )
        }

        result = result.copy(
            rating = Pair(feedbacks.map { it.value.rating }.sum(), feedbacks.size)
        )

        result = result.copy(
            images = storage.parkSpotPhotoGet(documentId)
        )

        currentLocation?.let {
            result = result.copy(
                distance = getDistance(
                    currentLocation = it,
                    destination = location
                )
            )
        }

        return result.copy(isFree = false)
    }

    private suspend fun getFreeSpotListCardDetails(
        documentId: String,
        location: GeoPoint,
        currentLocation: GeoPoint?
    ): SpotListCardDetails {
        var result = SpotListCardDetails(documentId = documentId)
        val doc = freeSpot.getDetails(documentId)
        doc?.let {
            result = result.copy(
                name = it.landMark,
                images = it.images,
            )
        }
        currentLocation?.let {
            result = result.copy(
                distance = getDistance(it, location)
            )
        }
        return result.copy(isFree = true)
    }

    private fun getDistance(currentLocation: GeoPoint, destination: GeoPoint): Double {
        val EARTH_RADIUS = 6371.0
        val dLat = Math.toRadians(destination.latitude - currentLocation.latitude)
        val dLon = Math.toRadians(destination.longitude - currentLocation.longitude)

        // Convert latitudes to radians
        val lat1Rad = Math.toRadians(currentLocation.latitude)
        val lat2Rad = Math.toRadians(destination.latitude)

        // Haversine formula
        val a = sin(dLat / 2).pow(2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(dLon / 2).pow(2)

        val c = 2 * asin(sqrt(a))

        return EARTH_RADIUS * c

    }

}