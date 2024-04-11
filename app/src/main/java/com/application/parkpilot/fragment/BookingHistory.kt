package com.application.parkpilot.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.viewModel.BookingHistoryViewModel

class BookingHistory: Fragment(R.layout.booking_history) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycleView)

        // viewModel creation
        val viewModel = ViewModelProvider(this)[BookingHistoryViewModel::class.java]

        viewModel.loadRecycler(requireContext(),recyclerView)
    }
}