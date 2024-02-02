package com.application.parkpilot.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.application.parkpilot.ParkPilotMapLegend
import com.application.parkpilot.R
import com.application.parkpilot.bottomSheet.VehicleType
import com.application.parkpilot.module.OSM
import com.application.parkpilot.viewModel.AuthenticationViewModel
import com.application.parkpilot.viewModel.HomeViewModel
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint


class HomeActivity : AppCompatActivity(R.layout.home) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // object creation and initialization of views
        val searchBar: SearchBar = findViewById(R.id.searchBar)
        val searchView: SearchView = findViewById(R.id.searchView)
        val currentLocationButton: Button = findViewById(R.id.buttonCurrentLocation)


        // getting authentication view model reference
        val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(this@HomeActivity) as T
            }
        })[HomeViewModel::class.java]

        // initializing OSM map object
        viewModel.setMapView(findViewById(R.id.mapViewOSM),this)

        // this method will add pins on map
        viewModel.loadMapViewPins(this,supportFragmentManager)

        // when search bar menu's items clicked
        searchBar.setOnMenuItemClickListener { clickedItem ->

            when (clickedItem.itemId) {
                R.id.logoutButton -> {
                    viewModel.logout(this)
                    Toast.makeText(this, "Logout Successfully", Toast.LENGTH_SHORT).show()
                    return@setOnMenuItemClickListener true
                }
                R.id.registerButton -> {
                    viewModel.register(this)
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    return@setOnMenuItemClickListener false
                }
            }
        }

        // when user will type in search bar and press search(action) button (present on keyboard)
        searchView.editText.setOnEditorActionListener { _, _, _ ->
            // setting the typed text to the search bar
            searchBar.setText(searchView.text)

            // hide the searchView(search suggestion box)
            searchView.hide()

            // creating co-routine scope to run search method
            viewModel.search((searchView.text.toString()))
            false
        }

        // when current location button press
        currentLocationButton.setOnClickListener {
            // it will set current location in mapView
            viewModel.getCurrentLocation()
        }
    }
}