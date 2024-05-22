package com.application.parkpilot.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.parkpilot.R
import com.application.parkpilot.databinding.BookingHistoryBinding
import com.application.parkpilot.viewModel.BookingHistoryViewModel

class BookingHistory : Fragment(R.layout.booking_history) {
    private lateinit var binding: BookingHistoryBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = BookingHistoryBinding.bind(view)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        // viewModel creation
        val viewModel = ViewModelProvider(this)[BookingHistoryViewModel::class.java]

        viewModel.loadRecycler(requireContext(), binding.recyclerView)
    }
}