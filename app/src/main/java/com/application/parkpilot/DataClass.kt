package com.application.parkpilot

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.GeoPoint as FirebaseGeoPoint
import org.osmdroid.util.GeoPoint as OSMGeoPoint


data class ParkPilotMapLegend(val title: String, val UID: String, val coordinates: OSMGeoPoint)
data class UserCollection(
    var firstName: String,
    var lastName: String,
    val birthDate: String,
    val gender: String
) {
    init {
        firstName = firstName.trim().replaceFirstChar(Char::uppercaseChar)
        lastName = lastName.trim().replaceFirstChar(Char::uppercaseChar)
    }
}

data class UserProfile(var userName: String, val userPicture: Uri? = null) {
    init {
        userName = userName.trim().replaceFirstChar(Char::uppercaseChar)
    }
}

data class QRCodeCollection(
    val key: String,
    val to: Int,
    val from: Timestamp = Timestamp.now(),
    val valid: Boolean = true
)

data class StationLocation(
    val stationUid: String?,
    val coordinates: FirebaseGeoPoint,
    val isFree: Boolean = false,
)

data class StationBasic(var name: String?, val price: Int?, val reserved: Int?) {
    init {
        name?.let {
            name = name?.trim()?.replaceFirstChar(Char::uppercaseChar)
        }
    }
}

data class StationAdvance(
    val policies: String,
    val amenities: List<String>,
    val accessHours: AccessHours,
)

data class Time(val hours: Int, val minute: Int)

data class AccessHours(
    val open: String,
    val close: String,
    val selectedDays: List<String>
)

data class FreeSpotDetails(
    val documentId: String = "",
    val landMark: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val policies: String = "",
    val images: List<Uri> = emptyList()
)

data class Book(
    val fromTimestamp: Timestamp,
    val toTimestamp: Timestamp,
    val stationID: String,
    val userID: String,
)

data class Ticket(
    val fromTimestamp: Timestamp,
    val toTimestamp: Timestamp,
    val stationID: String,
    val qrcode: String
)

data class Feedback(val UID: String = User.UID, val rating: Float, val message: String)