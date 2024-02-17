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

class SpotDetailActivity : AppCompatActivity(R.layout.spot_detail), PaymentResultListener {

    // late init view model property
    private lateinit var viewModel: SpotDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // view init
        val recyclerView: RecyclerView = findViewById(R.id.recycleView)
        val buttonBookNow: Button = findViewById(R.id.buttonBookNow)
        val buttonSelectTime:Button = findViewById(R.id.buttonSelectTime)

        // view model init
        viewModel = ViewModelProvider(this)[SpotDetailViewModel::class.java]

        // loading recycler view default (init) properties
        recyclerView.apply {
            layoutManager = CarouselLayoutManager()
            viewModel.loadCarousel(this@SpotDetailActivity, this)
        }

        //
        buttonBookNow.setOnClickListener {
            viewModel.bookNow(this)
        }

        //
        buttonSelectTime.setOnClickListener {
//            viewModel.setTime()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String) {
        viewModel.onPaymentSuccess(this, razorpayPaymentID)
    }

    override fun onPaymentError(code: Int, response: String) {
        Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show()
    }
}