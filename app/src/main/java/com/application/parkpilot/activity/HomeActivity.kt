package com.application.parkpilot.activity

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.application.parkpilot.R
import com.application.parkpilot.viewModel.HomeViewModel
import com.google.android.material.navigationrail.NavigationRailView
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView


class HomeActivity : AppCompatActivity(R.layout.home) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // to make transparent status bar
        //        val w = window
        //        w.setFlags(
        //            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        //            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        //        )
        //
        //        val decor = window.decorView;
        //        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)



        // object creation and initialization of views
        val searchBar: SearchBar = findViewById(R.id.searchBar)
        val searchView: SearchView = findViewById(R.id.searchView)
        val currentLocationButton: Button = findViewById(R.id.buttonCurrentLocation)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navigationRailView: NavigationRailView = findViewById(R.id.navigationRailView)


        // getting authentication view model reference
        val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(this@HomeActivity) as T
            }
        })[HomeViewModel::class.java]

        // load the profile image in search bar if present
        viewModel.loadProfileImage(this, searchBar.menu.findItem(R.id.searchbarProfileImage))


        // initializing OSM map object
        viewModel.setMapView(findViewById(R.id.mapViewOSM), this)

        // this method will add pins on map
        viewModel.loadMapViewPins(supportFragmentManager)

        // when search bar menu's items clicked
        navigationRailView.setOnItemSelectedListener { clickedItem ->

            when (clickedItem.itemId) {
                R.id.buttonLogout -> {
                    viewModel.logout(this)
                    drawerLayout.closeDrawer(GravityCompat.END)
                    Toast.makeText(this, "Logout Successfully", Toast.LENGTH_SHORT).show()
                    return@setOnItemSelectedListener true
                }

                R.id.buttonRegister -> {
                    viewModel.register(this)
                    drawerLayout.closeDrawer(GravityCompat.END)
                    return@setOnItemSelectedListener true
                }

                R.id.buttonHistory -> {
                    viewModel.history(this)
                    drawerLayout.closeDrawer(GravityCompat.END)
                    return@setOnItemSelectedListener true
                }

                else -> {
                    return@setOnItemSelectedListener false
                }
            }
        }

        searchBar.setOnMenuItemClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
            return@setOnMenuItemClickListener false
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