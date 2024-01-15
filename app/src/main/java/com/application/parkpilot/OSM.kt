package com.application.parkpilot

import android.location.Address
import android.location.Geocoder
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class OSM<Act : AppCompatActivity>(private val mapView: MapView, private val obj: Act) {

    // object creation of geocoder
    private val geocoder: Geocoder = Geocoder(obj)

    // object creation of fusedLocation
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(obj)

    init {

        mapView.setTileSource(TileSourceFactory.PUBLIC_TRANSPORT)
        mapView.controller.setZoom(17.0)
        mapView.setMultiTouchControls(true)

        //temp
        setCenter(18.50099198033669, 73.85907568230525)

        mapView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                onTouch(event)
            }
            false
        }
    }

    private fun onTouch(event: MotionEvent): Array<Double>? {
        if (event.action == MotionEvent.ACTION_UP) {
//            val projection = mapView.projection
//            val geoPoint = projection.fromPixels(event.x.toInt(), event.y.toInt())
//
//            mapView.invalidate() // Redraw the map
//
//            // Save the coordinates (latitude and longitude) for later use
//            return arrayOf(geoPoint.latitude, geoPoint.longitude)
            println("onTouch")
        }
        return null
    }

    fun setCenter(latitude: Double, longitude: Double) {
        mapView.controller.setCenter(GeoPoint(latitude, longitude))
        mapView.invalidate()
    }

    fun search(searchQuery: String): Address? {
        // this is deprecated in API 33
        return geocoder.getFromLocationName(searchQuery, 1)?.first()
    }

    fun getLastKnowLocation() {

//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location : Location? ->
//                // Got last known location. In some rare situations this can be null.
//                location?.
//            }
        // request for location permission
//        if(PermissionRequest(obj).locationPermissionRequest()) {
//            fusedLocationClient.lastLocation
//        }
    }
}