package com.application.parkpilot.viewModel

import android.net.Uri
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.parkpilot.StationBasic as StationBasic_DS
import com.application.parkpilot.StationLocation as StationLocation_DS
import com.application.parkpilot.StationAdvance as StationAdvance_DS
import com.application.parkpilot.User
import com.application.parkpilot.module.OSM
import com.application.parkpilot.module.PhotoPicker
import com.application.parkpilot.module.TimePicker
import com.application.parkpilot.module.firebase.Storage
import com.application.parkpilot.module.firebase.database.StationBasic
import com.google.firebase.firestore.GeoPoint as GeoPoint_FB
import com.application.parkpilot.module.firebase.database.StationAdvance as StationAdvance_FS
import com.application.parkpilot.module.firebase.database.StationBasic as StationBasic_FS
import com.application.parkpilot.module.firebase.database.StationLocation as StationLocation_FS
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint as GeoPoint_OSM
import org.osmdroid.views.MapView

class ParkRegisterViewModel(mapView: MapView, activity: AppCompatActivity) : ViewModel() {
    private var mapViewOSM: OSM<AppCompatActivity> = OSM(mapView, activity)
    private val marker = mapViewOSM.addMarker(mapViewOSM.center)
    val timePicker = TimePicker("pick the time",TimePicker.CLOCK_12H)
    val photoPicker = PhotoPicker(activity)
    val imageViewsUri = arrayOf<Uri?>(null,null,null)
    val liveDataStationBasic = MutableLiveData<StationBasic_DS?>()
    val liveDataStationAdvance = MutableLiveData<StationAdvance_DS?>()
    val result = MutableLiveData<Boolean>()
    val liveDataImages = MutableLiveData<List<Uri>?>()


    init {
        mapViewOSM.touchLocationObserver.observe(activity) {
            marker.position = it
        }
    }

    fun timePicker(manager: FragmentManager, tag:String?){
            timePicker.showTimePicker(manager,tag)
    }

    fun loadActivity(){
        viewModelScope.launch {
            liveDataStationBasic.value = StationBasic_FS().basicGet(User.UID)
            liveDataStationAdvance.value = StationAdvance_FS().advanceGet(User.UID)
            liveDataImages.value = Storage().parkSpotPhotoGet(User.UID)
        }
    }

    fun search(searchQuery: String) {
        // suspend function. it will block processes/UI thread ( you can run this function on another thread/coroutine)
        val address = mapViewOSM.search(searchQuery)
        // when search method got the search result without empty body
        address?.let {
            mapViewOSM.setCenter(GeoPoint_OSM(it.latitude, it.longitude))
        }
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            // suspend function. It will block the processes/UI thread
            val currentLocation = mapViewOSM.getLastKnowLocation()

            // when we got user current location
            currentLocation?.let {
                // set the user's current location as center of map
                mapViewOSM.setCenter(it)
                marker.position = it
            }
        }
    }

    fun imagePicker(){
        photoPicker.showPhotoPicker()
    }

    fun fillAddress(editText: EditText) {
        val address = mapViewOSM.getAddress(marker.position)
        address?.let {
            editText.setText(it.getAddressLine(0))
        }
    }

    fun uploadLocation() {
        val stationLocation = StationLocation_FS()

        viewModelScope.launch {
            marker.position.apply {
                stationLocation.locationSet(
                    StationLocation_DS(null, GeoPoint_FB(latitude,longitude)),
                    User.UID
                )
            }
        }
    }

    fun uploadBasic(stationName:String,startingPrice:Int){
        val stationBasic = StationBasic_FS()
        viewModelScope.launch{
            stationBasic.basicSet(StationBasic_DS(stationName,startingPrice,null),User.UID)
        }
    }

    fun uploadAdvance(info:StationAdvance_DS){
        val stationAdvance = StationAdvance_FS()
        viewModelScope.launch{
            stationAdvance.advanceSet(info,User.UID)
        }
    }

    fun uploadImages(){
        viewModelScope.launch {
            Storage().parkSpotPhotoPut(User.UID, imageViewsUri)
        }
    }
}