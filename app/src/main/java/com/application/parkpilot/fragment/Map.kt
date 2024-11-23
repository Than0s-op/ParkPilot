package com.application.parkpilot.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.lifecycle.ViewModelProvider
import com.application.parkpilot.R
import com.application.parkpilot.databinding.MapBinding
import com.application.parkpilot.viewModel.MapViewModel
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView

class Map : Fragment(R.layout.map) {

    private lateinit var binding: MapBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = MapBinding.bind(view)

        // getting authentication view model reference
        val viewModel = ViewModelProvider(this)[MapViewModel::class.java]

        // initializing OSM map object
        viewModel.setMapView(binding.mapViewOSM)

        // this method will add pins on map
        viewModel.loadMapViewPins(requireContext(), parentFragmentManager)

        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.progressBarSearch.visibility = View.VISIBLE
                viewModel.search(
                    requireContext(),
                    binding.searchView.query.toString()
                ) {
                    binding.progressBarSearch.visibility = View.GONE
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        // when current location button press
        binding.buttonCurrentLocation.setOnClickListener {
            // it will set current location in mapView
            viewModel.getCurrentLocation(requireContext())
        }
    }
}