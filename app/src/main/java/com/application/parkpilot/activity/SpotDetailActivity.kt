package com.application.parkpilot.activity

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.viewModel.SpotDetailViewModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.razorpay.PaymentResultListener

class SpotDetailActivity : AppCompatActivity(R.layout.spot_detail) {

    // late init view model property
    private lateinit var viewModel: SpotDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // view init
        val recyclerView: RecyclerView = findViewById(R.id.recycleView)

        // view model init
        viewModel = ViewModelProvider(this)[SpotDetailViewModel::class.java]

        // loading recycler view default (init) properties
        recyclerView.apply {
            layoutManager = CarouselLayoutManager()
            viewModel.loadCarousel(this@SpotDetailActivity, this)
        }

    }
}