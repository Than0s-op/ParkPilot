package com.application.parkpilot.viewModel

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.parkpilot.ParkPilotMapLegend
import com.application.parkpilot.Utils
import com.application.parkpilot.fragment.bottomSheet.SpotPreview
import com.application.parkpilot.module.OSM
import com.application.parkpilot.module.firebase.database.FreeSpot
import com.application.parkpilot.module.firebase.database.StationLocation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class MapViewModel : ViewModel() {
    // this object will access by two function
    private lateinit var mapViewOSM: OSM

    fun setMapView(mapView: MapView) {
        // initializing OSM map object
        mapViewOSM = OSM(mapView)
    }

    fun search(context: Context, searchQuery: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            // suspend function. it will block processes/UI thread ( you can run this function on another thread/coroutine)
            val address = mapViewOSM.search(context, searchQuery)
            // when search method got the search result without empty body
            address?.let {
                mapViewOSM.setCenter(GeoPoint(it.latitude, it.longitude))
            } ?: Toast.makeText(context, "No result found", Toast.LENGTH_SHORT).show()
            delay(2000)
            onComplete()
        }
    }

    fun getCurrentLocation(context: Context) {
        viewModelScope.launch {
            // suspend function. It will block the processes/UI thread
            val currentLocation = Utils.getLastKnowLocation(context)
            // when we got user current location
            currentLocation?.let {
                // set the user's current location as center of map
                mapViewOSM.setCenter(GeoPoint(it.latitude, it.longitude))
            }
        }
    }

    fun loadMapViewPins(context: Context, supportFragmentManager: FragmentManager) {

        // creating the lambda function to pass with "set pins on position" method
        val singleTapTask = { uID: String, isFreeSpot: Boolean ->
            SpotPreview(isFreeSpot).show(supportFragmentManager, uID)
        }

        viewModelScope.launch {
            val stationsCoordinates = StationLocation().locationGet()

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
                        GeoPoint(info.coordinates.latitude, info.coordinates.longitude),
                        isFreeSpot = info.isFree
                    )
                )
            }

            val freeSpots = FreeSpot().getLocations()
            // iterate the collection
            for (info in freeSpots) {
                // adding "ParkPilotMapLegend" data-class object in arraylist
                stations.add(
                    ParkPilotMapLegend(
                        "Free Spot",
                        info.stationUid!!,
                        // converting firebase geoPoint to OSM geoPoint
                        GeoPoint(info.coordinates.latitude, info.coordinates.longitude),
                        isFreeSpot = info.isFree
                    )
                )
            }

            // this method will add pins on map with single tap behaviour
            mapViewOSM.setPinsOnPosition(context, stations, singleTapTask)
        }
    }
}