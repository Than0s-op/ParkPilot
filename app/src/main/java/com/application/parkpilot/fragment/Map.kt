package com.application.parkpilot.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.application.parkpilot.R
import com.application.parkpilot.viewModel.MapViewModel
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView

class Map : Fragment(R.layout.map) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchBar: SearchBar = view.findViewById(R.id.searchBar)
        val searchView: SearchView = view.findViewById(R.id.searchView)
        val currentLocationButton: Button = view.findViewById(R.id.buttonCurrentLocation)

        // getting authentication view model reference
        val viewModel = ViewModelProvider(this)[MapViewModel::class.java]

        // initializing OSM map object
        viewModel.setMapView(view.findViewById(R.id.mapViewOSM))

        // this method will add pins on map
        viewModel.loadMapViewPins(requireContext(),parentFragmentManager)

        // when user will type in search bar and press search(action) button (present on keyboard)
        searchView.editText.setOnEditorActionListener { _, _, _ ->
            // setting the typed text to the search bar
            searchBar.setText(searchView.text)

            // hide the searchView(search suggestion box)
            searchView.hide()

            // creating co-routine scope to run search method
            viewModel.search(requireContext(),searchView.text.toString())
            false
        }

        // when current location button press
        currentLocationButton.setOnClickListener {
            // it will set current location in mapView
            viewModel.getCurrentLocation(requireContext())
        }
    }
}