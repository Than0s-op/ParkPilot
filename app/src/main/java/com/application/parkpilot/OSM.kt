package com.application.parkpilot

import android.R
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener
import org.osmdroid.views.overlay.ItemizedOverlay
import org.osmdroid.views.overlay.OverlayItem


class OSM<Act : AppCompatActivity>(private val mapView: MapView, private val obj: Act) {

    // object creation of geocoder
    private val geocoder: Geocoder = Geocoder(obj)

    // object creation of fusedLocation
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(obj)

    init {

        // mapView default settings
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.controller.setZoom(17.0)
        mapView.setMultiTouchControls(true)
        mapView.isClickable = true

        // temp
        setCenter(18.50099198033669, 73.85907568230525)

//        mapView.setOnTouchListener { _, event ->
//            if (event.action == MotionEvent.ACTION_UP) {
//                onTouch(event)
//            }
//            false
//        }

    }

//    private fun onTouch(event: MotionEvent): Array<Double>? {
//        if (event.action == MotionEvent.ACTION_UP) {
////            val projection = mapView.projection
////            val geoPoint = projection.fromPixels(event.x.toInt(), event.y.toInt())
////
////            mapView.invalidate() // Redraw the map
////
////            // Save the coordinates (latitude and longitude) for later use
////            return arrayOf(geoPoint.latitude, geoPoint.longitude)
//        }
//        return null
//    }

    fun setCenter(latitude: Double, longitude: Double) {
        mapView.controller.setCenter(GeoPoint(latitude, longitude))
    }

    fun search(searchQuery: String): Address? {
        // this is deprecated in API 33
        val result = geocoder.getFromLocationName(searchQuery,1)
        if(result != null && result.size != 0){
            return result.first()
        }
        return null
    }

    suspend fun getLastKnowLocation():GeoPoint? {

        // permission class object creation
        val obj = PermissionRequest(obj)

        // current location set to null (if permission is not granted, we can't enter in "if" block)
        var currentLocation:GeoPoint? = null

        // check is location permission granted or not. If Not request to user for location permission
        if (obj.locationPermissionRequest()) {

            // request to turn on location(GPS)
            obj.GPSPermissionRequest()

            // below code will give last know location of user. But It will "null" if user's GPS is turned off
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if(location != null){
                        currentLocation = GeoPoint(location.latitude,location.longitude)
                    }
                }.await()
        }
        return currentLocation
    }

    fun setMarkerOnPosition(geoPoint:GeoPoint) {

        val overlayItem = OverlayItem("San Fransisco", "California", geoPoint)

        val markerDrawable = ContextCompat.getDrawable(obj, com.application.parkpilot.R.drawable.park_pilot_map_marker)
        overlayItem.setMarker(markerDrawable)

        val overlayItemArrayList = ArrayList<OverlayItem>()
        overlayItemArrayList.add(overlayItem)
        val locationOverlay: ItemizedOverlay<OverlayItem> = ItemizedIconOverlay(
            overlayItemArrayList,
            object : OnItemGestureListener<OverlayItem> {
                override fun onItemSingleTapUp(i: Int, overlayItem: OverlayItem): Boolean {
                    println("Single Tap Up on maker")
                    return true // Handled this event.
                }

                override fun onItemLongPress(i: Int, overlayItem: OverlayItem): Boolean {
                    println("Long press on marker")
                    return false
                }
            },
            obj
        )

        this.mapView.overlays.add(locationOverlay)
        mapView.overlays.add(locationOverlay)
    }
}