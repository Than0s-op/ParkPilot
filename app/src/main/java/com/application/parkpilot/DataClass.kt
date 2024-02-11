package com.application.parkpilot

import android.net.Uri
import com.google.firebase.Timestamp
import org.osmdroid.util.GeoPoint


data class ParkPilotMapLegend(val title: String, val UID: String, val coordinates: GeoPoint)
data class UserCollection(
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val gender: String
)

data class UserProfile(val displayName: String, val photoUri: Uri)
data class QRCodeCollection(
    val key: String,
    val upTo: Int,
    val generate: Timestamp = Timestamp.now(),
    val valid: Boolean = true
)