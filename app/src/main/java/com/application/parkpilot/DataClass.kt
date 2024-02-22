package com.application.parkpilot

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint as FirebaseGeoPoint
import org.osmdroid.util.GeoPoint as OSMGeoPoint


data class ParkPilotMapLegend(val title: String, val UID: String, val coordinates: OSMGeoPoint)
data class UserCollection(
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val gender: String
)

data class UserProfile(val displayName: String, val photoUri: Uri)
data class QRCodeCollection(
    val key: String,
    val to: Int,
    val from: Timestamp = Timestamp.now(),
    val valid: Boolean = true
)

data class StationLocation(val stationUid: String, val coordinates: FirebaseGeoPoint)
data class StationBasic(val name: String, val price: Int, val rating: Float)

// think should you know = tSYK
data class StationAdvance(
    val TSYK: ArrayList<String>,
    val amenities: ArrayList<String>,
    val accessHours: String,
    val gettingThere:String,
)