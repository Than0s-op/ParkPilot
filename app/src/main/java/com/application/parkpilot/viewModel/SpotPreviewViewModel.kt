package com.application.parkpilot.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.adapter.CarouselRecyclerView
import com.google.android.material.carousel.CarouselLayoutManager

class SpotPreviewViewModel: ViewModel() {

    var carouselImages = MutableLiveData<ArrayList<Any>>()
        private set

    fun loadCarousel(){
        val images: ArrayList<Any> = ArrayList()
        images.apply {
            add("https://images.pexels.com/photos/1545743/pexels-photo-1545743.jpeg?auto=compress&cs=tinysrgb&w=600")
            add("https://images.pexels.com/photos/116675/pexels-photo-116675.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1")
            add("https://images.pexels.com/photos/707046/pexels-photo-707046.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1")
            add("https://images.pexels.com/photos/164634/pexels-photo-164634.jpeg?auto=compress&cs=tinysrgb&w=600")
        }
        carouselImages.value = images
    }
}