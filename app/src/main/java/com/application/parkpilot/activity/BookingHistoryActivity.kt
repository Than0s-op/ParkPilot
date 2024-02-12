package com.application.parkpilot.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.viewModel.BookingHistoryViewModel

class BookingHistoryActivity : AppCompatActivity(R.layout.booking_history) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recyclerView:RecyclerView = findViewById(R.id.recycleView)

        // viewModel creation
        val viewModel = ViewModelProvider(this)[BookingHistoryViewModel::class.java]

        viewModel.loadRecycler(this,recyclerView)
    }
}