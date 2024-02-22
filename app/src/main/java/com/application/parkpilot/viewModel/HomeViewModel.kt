package com.application.parkpilot.viewModel

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.parkpilot.ParkPilotMapLegend
import com.application.parkpilot.activity.AuthenticationActivity
import com.application.parkpilot.activity.BookingHistoryActivity
import com.application.parkpilot.activity.HomeActivity
import com.application.parkpilot.activity.UserRegisterActivity
import com.application.parkpilot.bottomSheet.SpotPreview
import com.application.parkpilot.module.OSM
import com.application.parkpilot.module.PhotoLoader
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import com.application.parkpilot.module.firebase.database.StationLocation as FireStoreStationLocation

class HomeViewModel(activity: HomeActivity) : ViewModel() {


    // this object will access by two function
    private lateinit var mapViewOSM: OSM<HomeActivity>

    // it will store user status
    val isAnonymous = Firebase.auth.currentUser == null

    fun setMapView(mapView: MapView, activity: HomeActivity) {
        // initializing OSM map object
        mapViewOSM = OSM(mapView, activity)
    }

    fun logout(context: Context) {
        // sign out the user
        Firebase.auth.signOut()

        // creating the intent of Authentication activity
        val intent = Intent(context, AuthenticationActivity::class.java).apply {
            // to clear activity stack
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // start the activity
        context.startActivity(intent)
    }

    fun search(address: String) {
        viewModelScope.launch {
            // suspend function. it will block processes/UI thread ( you can run this function on another thread/coroutine)
            val address = mapViewOSM.search(address)
            // when search method got the search result without empty body
            address?.let {
                mapViewOSM.setCenter(it.latitude, it.longitude)
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
                mapViewOSM.setCenter(it.latitude, it.longitude)
            }
        }
    }

    fun loadProfileImage(context: Context, profileImage: MenuItem) {
        Firebase.auth.currentUser?.photoUrl?.let {
            viewModelScope.launch {
                profileImage.icon = PhotoLoader().getImage(context, it).drawable
            }
        }
    }

    fun register(context: Context) {
        context.startActivity(Intent(context, UserRegisterActivity::class.java))
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
                        info.stationUid,
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