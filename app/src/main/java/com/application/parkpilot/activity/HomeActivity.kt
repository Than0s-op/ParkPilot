package com.application.parkpilot.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.parkpilot.ParkPilotMapLegend
import com.application.parkpilot.R
import com.application.parkpilot.bottom_sheet.VehicleType
import com.application.parkpilot.module.OSM
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint


class HomeActivity : AppCompatActivity() {

    // this object will access by two function
    lateinit var OSMMap: OSM<HomeActivity>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        // object creation and initialization of views
        val searchBar: SearchBar = findViewById(R.id.searchBar)
        val searchView: SearchView = findViewById(R.id.searchView)
        val currentLocationButton: Button = findViewById(R.id.buttonCurrentLocation)

        // initializing OSM map object
        OSMMap = OSM(findViewById(R.id.OSMMapView), this)

        // this method will add pins on map
        loadMapViewPins()

        // when search bar menu's items clicked
        searchBar.setOnMenuItemClickListener { clickedItem ->

            when (clickedItem.itemId) {
                R.id.logoutButton -> {
                    // sign out the user
                    Firebase.auth.signOut()

                    // creating the intent of Authentication activity
                    val intent = Intent(this, AuthenticationActivity::class.java)

                    // to clear activity stack
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    Toast.makeText(this, "Logout Successfully", Toast.LENGTH_SHORT).show()
                    startActivity(intent)

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
            CoroutineScope(Dispatchers.Main).launch {
                // suspend function. it will block processes/UI thread ( you can run this function on another thread/coroutine)
                val address = OSMMap.search(searchView.text.toString())

                // when search method got the search result without empty body
                if (address != null) {
                    OSMMap.setCenter(address.latitude, address.longitude)
                } else {
                    Toast.makeText(baseContext, "Invalid request", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            false
        }

        // when current location button press
        currentLocationButton.setOnClickListener {
            // creating co-routine scope to run getLastKnowLocation method
            CoroutineScope(Dispatchers.Default).launch {
                // suspend function. It will block the processes/UI thread
                val currentLocation = OSMMap.getLastKnowLocation()

                // when we got user current location
                if (currentLocation != null) {
                    // set the user's current location as center of map
                    OSMMap.setCenter(currentLocation.latitude, currentLocation.longitude)
                }
            }
        }
    }

    private fun loadMapViewPins() {
        // Initializing fire store
        val fireStore = Firebase.firestore

        // creating the lambda function to pass with "set pins on position" method
        val singleTapTask = { UID: String ->
            VehicleType().show(supportFragmentManager,null)
        }

        // fetching data from fire-store's "@string/station" collection
        fireStore.collection(getString(R.string.station)).get()
            // collection get successfully
            .addOnSuccessListener { collection ->

                // creating arraylist of ParkPilotMapPin (title,UID,GeoPoint) "data class"
                val mapViewPins = ArrayList<ParkPilotMapLegend>()

                // iterate the collection
                for (document in collection) {

                    // parsing document to get GeoPoint
                    val geoPoint =
                        document.data["location"] as com.google.firebase.firestore.GeoPoint

                    // adding "ParkPilotMapPin" data-class object in arraylist
                    mapViewPins.add(
                        ParkPilotMapLegend(
                            "Park Pilot pin",
                            document.id, GeoPoint(geoPoint.latitude, geoPoint.longitude)
                        )
                    )
                }

                // this method will add pins on map with single tap behaviour
                OSMMap.setPinsOnPosition(mapViewPins, singleTapTask)
            }
            // failed to get collection
            .addOnFailureListener {
                println("${it.message}")
            }
    }
}