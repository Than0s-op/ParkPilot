package com.application.parkpilot.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.parkpilot.R
import com.application.parkpilot.adapter.recycler.History
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

        viewModel.loadRecycler(
            requireContext(),
            binding.recyclerView,
        ) {
            hideShimmer()
            if (viewModel.bookingList.isEmpty()) {
                binding.noHistoryTextView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.noHistoryTextView.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }

        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = viewModel.bookingList.filter {
                    it.stationID.contains(newText ?: "", true)
                }
                binding.recyclerView.adapter =
                    History(requireContext(), R.layout.history_card, ArrayList(filteredList))
                return true
            }
        })
    }

    private fun hideShimmer() {
        binding.shimmerScrollView.visibility = View.GONE
    }
}