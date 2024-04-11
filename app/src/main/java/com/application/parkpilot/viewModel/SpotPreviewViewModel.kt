package com.application.parkpilot.viewModel

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.parkpilot.activity.Feedback
import com.application.parkpilot.module.firebase.Storage
import com.application.parkpilot.module.firebase.database.StationAdvance
import com.application.parkpilot.module.firebase.database.StationBasic
import com.application.parkpilot.module.firebase.database.StationLocation
import com.google.firebase.firestore.GeoPoint
import com.application.parkpilot.module.firebase.database.Feedback as FS_Feedback
import kotlinx.coroutines.launch
import com.application.parkpilot.StationAdvance as StationAdvanceDataClass
import com.application.parkpilot.StationBasic as StationBasicDataClass

class SpotPreviewViewModel : ViewModel() {

    val carouselImages = MutableLiveData<List<Any>>()
    val stationBasicInfo = MutableLiveData<StationBasicDataClass>()
    val stationAdvanceInfo = MutableLiveData<StationAdvanceDataClass>()
    val stationRating = MutableLiveData<Pair<Float,Int>>()
    val stationLocation = MutableLiveData<GeoPoint>()

    fun loadCarousel(stationUID:String) {
        viewModelScope.launch {
            carouselImages.value = Storage().parkSpotPhotoGet(stationUID)
        }
    }

    fun loadBasicInfo(stationUID: String) {
        viewModelScope.launch {
            stationBasicInfo.value = StationBasic().basicGet(stationUID)
        }
    }

    fun loadAdvanceInfo(stationUID: String) {
        viewModelScope.launch {
            stationAdvanceInfo.value = StationAdvance().advanceGet(stationUID)
        }
    }

    fun getStationLocation(stationUID:String){
        viewModelScope.launch{
            stationLocation.value = StationLocation().locationGet(stationUID)
        }
    }

    fun loadRating(stationUID:String){
        viewModelScope.launch {
            val feedbacks = FS_Feedback().feedGet(stationUID)
            var totalRatting = 0.0f
            for(i in feedbacks){
                totalRatting += i.value.rating
            }
            stationRating.value = Pair(totalRatting,feedbacks.size)
        }
    }

    fun feedback(context: Context,stationUid:String){
        context.startActivity(Intent(context, Feedback::class.java).apply{
            putExtra("stationUID",stationUid)
        })
    }
}