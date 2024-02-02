package com.application.parkpilot.viewModel

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.getString
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.parkpilot.ParkPilotMapLegend
import com.application.parkpilot.R
import com.application.parkpilot.activity.AuthenticationActivity
import com.application.parkpilot.activity.HomeActivity
import com.application.parkpilot.activity.UserRegisterActivity
import com.application.parkpilot.bottomSheet.VehicleType
import com.application.parkpilot.module.OSM
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class HomeViewModel(activity: HomeActivity) : ViewModel() {


    // this object will access by two function
    private lateinit var mapViewOSM: OSM<HomeActivity>

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

    fun register(context:Context){
        context.startActivity(Intent(context, UserRegisterActivity::class.java))
    }

    fun loadMapViewPins(context: Context, supportFragmentManager: FragmentManager) {
        // Initializing fire store
        val fireStore = Firebase.firestore

        // creating the lambda function to pass with "set pins on position" method
        val singleTapTask = { UID: String ->
            VehicleType().show(supportFragmentManager, null)
        }

        // fetching data from fire-store's "@string/station" collection
        fireStore.collection(getString(context, R.string.station)).get()
            // collection get successfully
            .addOnSuccessListener { collection ->

                // creating arraylist of ParkPilotMapPin (title,UID,GeoPoint) "data class"
                val mapViewPins = ArrayList<ParkPilotMapLegend>()

                // iterate the collection
                for (document in collection) {

                    // parsing document to get GeoPoint
                    val geoPoint =
                        document.data["location"] as com.google.firebase.firestore.GeoPoint

                    // adding "ParkPilotMapPin" data-class object in arraylist
                    mapViewPins.add(
                        ParkPilotMapLegend(
                            "Park Pilot pin",
                            document.id, GeoPoint(geoPoint.latitude, geoPoint.longitude)
                        )
                    )
                }

                // this method will add pins on map with single tap behaviour
                mapViewOSM.setPinsOnPosition(mapViewPins, singleTapTask)
            }
            // failed to get collection
            .addOnFailureListener {
                println("${it.message}")
            }
    }
}