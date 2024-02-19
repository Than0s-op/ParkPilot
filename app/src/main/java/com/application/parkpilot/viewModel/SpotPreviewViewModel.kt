package com.application.parkpilot.viewModel

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.parkpilot.StationBasic
import com.application.parkpilot.module.firebase.database.Station
import kotlinx.coroutines.launch
import java.io.Serializable

class SpotPreviewViewModel : ViewModel() {

    val carouselImages = MutableLiveData<ArrayList<Any>>()
    val stationBasicInfo = MutableLiveData<StationBasic>()

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

    fun loadInfo(stationUID:String) {
        viewModelScope.launch {
            stationBasicInfo.value = Station().basicGet(stationUID)
        }
    }


}