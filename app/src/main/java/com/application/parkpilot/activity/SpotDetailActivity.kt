package com.application.parkpilot.activity

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.QRCodeCollection
import com.application.parkpilot.R
import com.application.parkpilot.adapter.CarouselRecyclerView
import com.application.parkpilot.module.QRGenerator
import com.application.parkpilot.module.RazorPay
import com.application.parkpilot.view.DialogQRCode
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.razorpay.PaymentResultListener
import com.application.parkpilot.module.firebase.database.QRCode
import com.application.parkpilot.viewModel.SpotDetailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SpotDetailActivity : AppCompatActivity(R.layout.spot_detail), PaymentResultListener {

    // late init view model property
    private lateinit var viewModel: SpotDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // view init
        val recyclerView: RecyclerView = findViewById(R.id.recycleView)
        val buttonBookNow: Button = findViewById(R.id.buttonBookNow)

        // view model init
        viewModel = ViewModelProvider(this)[SpotDetailViewModel::class.java]

        // loading recycler view default (init) properties
        recyclerView.apply{
            layoutManager = CarouselLayoutManager()
            viewModel.loadCarousel(this@SpotDetailActivity,this)
        }

        buttonBookNow.setOnClickListener {
            viewModel.bookNow(this)
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String) {
        viewModel.onPaymentSuccess(this,razorpayPaymentID)
    }

    override fun onPaymentError(code: Int, response: String) {
        Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show()
    }
}