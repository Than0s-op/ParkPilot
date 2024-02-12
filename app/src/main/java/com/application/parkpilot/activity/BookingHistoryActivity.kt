package com.application.parkpilot.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.adapter.CarouselRecyclerView
import com.application.parkpilot.adapter.HistoryRecyclerView
import com.application.parkpilot.module.firebase.database.QRCode
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class BookingHistoryActivity : AppCompatActivity(R.layout.booking_history) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}