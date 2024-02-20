package com.application.parkpilot.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.adapter.CarouselRecyclerView
import com.application.parkpilot.viewModel.SpotPreviewViewModel
import com.google.android.material.carousel.CarouselLayoutManager

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


        val viewModel = ViewModelProvider(this)[SpotPreviewViewModel::class.java]

        // loading recycler view default (init) properties
        recyclerView.apply {
            layoutManager = CarouselLayoutManager()
            viewModel.loadCarousel()
        }

        viewModel.carouselImages.observe(this) { images ->
            recyclerView.adapter =
                CarouselRecyclerView(this, R.layout.square_carousel, images)
        }
    }
}