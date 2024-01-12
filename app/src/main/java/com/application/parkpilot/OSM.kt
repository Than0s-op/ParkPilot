package com.application.parkpilot

import android.app.Activity
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class OSM<Act : AppCompatActivity>(val mapView: MapView, private val obj:Act) {
    init{
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.controller.setZoom(15.0)
        mapView.setMultiTouchControls(true)

        //temp
        mapView.controller.setCenter(GeoPoint(18.50099198033669, 73.85907568230525))
        mapView.mapCenter

        mapView.invalidate()
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
}