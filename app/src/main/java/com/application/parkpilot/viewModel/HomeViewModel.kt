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

class HomeViewModel(activity: HomeActivity) : ViewModel() {


    // this object will access by two function
    private lateinit var mapViewOSM: OSM<HomeActivity>

    fun setMapView(mapView: MapView, activity: HomeActivity) {
        // initializing OSM map object
        mapViewOSM = OSM(mapView, activity)
    }

    fun search(searchQuery: String) {
        viewModelScope.launch {
            // suspend function. it will block processes/UI thread ( you can run this function on another thread/coroutine)
            val address = mapViewOSM.search(searchQuery)
            // when search method got the search result without empty body
            address?.let {
                mapViewOSM.setCenter(GeoPoint(it.latitude, it.longitude))
            }
        }
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            // suspend function. It will block the processes/UI thread
            val currentLocation = mapViewOSM.getLastKnowLocation()

            // when we got user current location
            currentLocation?.let {
                // set the user's current location as center of map
                mapViewOSM.setCenter(it)
            }
        }
    }

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

    fun loadMapViewPins(supportFragmentManager: FragmentManager) {

        // creating the lambda function to pass with "set pins on position" method
        val singleTapTask = { uID: String ->
            SpotPreview().show(supportFragmentManager, uID)
        }

        viewModelScope.launch {
            val stationsCoordinates = FireStoreStationLocation().locationGet()

            // creating arraylist of ParkPilotMapPin (title,UID,GeoPoint) "data class"
            val stations = ArrayList<ParkPilotMapLegend>()

            // iterate the collection
            for (info in stationsCoordinates) {
                // adding "ParkPilotMapLegend" data-class object in arraylist
                stations.add(
                    ParkPilotMapLegend(
                        "Station",
                        info.stationUid!!,
                        // converting firebase geoPoint to OSM geoPoint
                        GeoPoint(info.coordinates.latitude, info.coordinates.longitude)
                    )
                )
            }

            // this method will add pins on map with single tap behaviour
            mapViewOSM.setPinsOnPosition(stations, singleTapTask)
        }
    }
}