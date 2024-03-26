package com.application.parkpilot.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.adapter.CarouselRecyclerView
import com.application.parkpilot.viewModel.SpotPreviewViewModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class SpotDetailActivity : AppCompatActivity(R.layout.spot_detail) {

    // late init view model property
    private lateinit var viewModel: SpotPreviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // view init
        val recyclerView: RecyclerView = findViewById(R.id.recycleView)
        val textViewName: TextView = findViewById(R.id.textViewName)
        val textViewRating: TextView = findViewById(R.id.textViewRating)
        val textViewDistance: TextView = findViewById(R.id.textViewDistance)
        val textViewPrice: TextView = findViewById(R.id.textViewPrice)
        val textViewTSYK:TextView = findViewById(R.id.textViewTSYK)
        val textViewAccessHours:TextView = findViewById(R.id.textViewAccessHours)
        val textViewGettingThere:TextView = findViewById(R.id.textViewGettingThere)
        val buttonFeedback:ExtendedFloatingActionButton = findViewById(R.id.buttonFeedback)


        val viewModel = ViewModelProvider(this)[SpotPreviewViewModel::class.java]
        val stationUID = intent.getStringExtra("stationUID")!!

        viewModel.loadAdvanceInfo(stationUID)
        viewModel.loadBasicInfo(stationUID)
        viewModel.loadRating(stationUID)

        // loading recycler view default (init) properties
        recyclerView.apply {
            layoutManager = CarouselLayoutManager()
            viewModel.loadCarousel()
        }

        buttonFeedback.setOnClickListener{
            viewModel.feedback(this,stationUID)
        }

        viewModel.carouselImages.observe(this) { images ->
            recyclerView.adapter =
                CarouselRecyclerView(this, R.layout.square_carousel, images)
        }

        viewModel.stationAdvanceInfo.observe(this){
            // loading "Think should you know" section
            loadThinkShouldYouKnow(textViewTSYK,it.thinkShouldYouKnow)

            // loading amenities section
            loadAmenities(it.amenities)

            // loading access hours section
//            textViewAccessHours.text = it.accessHours

            // loading getting there section
            textViewGettingThere.text = it.gettingThere
        }

        viewModel.stationBasicInfo.observe(this){
            textViewName.text = it.name
            textViewRating.text = it.rating.toString()
            textViewPrice.text = it.price.toString()
        }

        viewModel.stationRating.observe(this){
            textViewRating.text = it
        }
    }

    private fun loadThinkShouldYouKnow(textViewTSYK:TextView, thinkShouldYouKnowList:List<String>){
        var thinkShouldYouKnow = ""
        for(line in thinkShouldYouKnowList){
            // it is just a formatting
            thinkShouldYouKnow += "• $line\n\n"
        }
        textViewTSYK.text = thinkShouldYouKnow
    }

    private fun loadAmenities(amenitiesList:List<String>){
        for(amenities in amenitiesList){
            when(amenities){
                "valet" -> findViewById<TextView>(R.id.textViewValet).visibility = View.VISIBLE
                "ev_charging" -> findViewById<TextView>(R.id.textViewEV).visibility = View.VISIBLE
            }
        }
    }
}