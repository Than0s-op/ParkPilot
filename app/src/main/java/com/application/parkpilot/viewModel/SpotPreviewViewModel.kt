package com.application.parkpilot.viewModel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.parkpilot.activity.Feedback
import com.application.parkpilot.module.firebase.database.StationAdvance
import com.application.parkpilot.module.firebase.database.StationBasic
import com.application.parkpilot.module.firebase.database.Feedback as FS_Feedback
import kotlinx.coroutines.launch
import com.application.parkpilot.StationAdvance as StationAdvanceDataClass
import com.application.parkpilot.StationBasic as StationBasicDataClass

class SpotPreviewViewModel : ViewModel() {

    val carouselImages = MutableLiveData<ArrayList<Any>>()
    val stationBasicInfo = MutableLiveData<StationBasicDataClass>()
    val stationAdvanceInfo = MutableLiveData<StationAdvanceDataClass>()
    val stationRating = MutableLiveData<String>()

    fun loadCarousel() {
        val images: ArrayList<Any> = ArrayList()
        images.apply {
            add("https://images.pexels.com/photos/1545743/pexels-photo-1545743.jpeg?auto=compress&cs=tinysrgb&w=600")
            add("https://images.pexels.com/photos/116675/pexels-photo-116675.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1")
            add("https://images.pexels.com/photos/707046/pexels-photo-707046.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1")
            add("https://images.pexels.com/photos/164634/pexels-photo-164634.jpeg?auto=compress&cs=tinysrgb&w=600")
        }
        carouselImages.value = images
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

    fun loadRating(stationUID:String){
        viewModelScope.launch {
            val feedbacks = FS_Feedback().feedGet(stationUID)
            var totalRatting = 0.0f
            for(i in feedbacks){
                totalRatting += i.value.rating
            }
            if(feedbacks.isNotEmpty())
                stationRating.value = String.format("%.1f",totalRatting / feedbacks.size)
            else stationRating.value = "N/A"
        }
    }

    fun feedback(context: Context,stationUid:String){
        context.startActivity(Intent(context, Feedback::class.java).apply{
            putExtra("stationUID",stationUid)
        })
    }
}