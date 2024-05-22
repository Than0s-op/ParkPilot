package com.application.parkpilot.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.application.parkpilot.R
import com.application.parkpilot.databinding.MapBinding
import com.application.parkpilot.viewModel.MapViewModel
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView

class Map : Fragment(R.layout.map) {

    private lateinit var binding:MapBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = MapBinding.bind(view)

        // getting authentication view model reference
        val viewModel = ViewModelProvider(this)[MapViewModel::class.java]

        // initializing OSM map object
        viewModel.setMapView(binding.mapViewOSM)

        // this method will add pins on map
        viewModel.loadMapViewPins(requireContext(),parentFragmentManager)

        // when user will type in search bar and press search(action) button (present on keyboard)
        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            // setting the typed text to the search bar
            binding.searchBar.setText(binding.searchView.text)

            // hide the searchView(search suggestion box)
            binding.searchView.hide()

            // creating co-routine scope to run search method
            viewModel.search(requireContext(),binding.searchView.text.toString())
            false
        }

        // when current location button press
        binding.buttonCurrentLocation.setOnClickListener {
            // it will set current location in mapView
            viewModel.getCurrentLocation(requireContext())
        }
    }
}