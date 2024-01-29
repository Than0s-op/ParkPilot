package com.application.parkpilot

import android.net.Uri
import org.osmdroid.util.GeoPoint


data class ParkPilotMapLegend(val title:String,val UID:String,val coordinates:GeoPoint)
data class UserCollection(val firstName:String,val lastName:String,val birthDate:String,val gender:String)
data class UserProfile(val displayName:String,val photoUri: Uri)