package com.application.parkpilot

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        Configuration.getInstance().userAgentValue = "ParkPilot"


        val OSMMap = OSM(findViewById(R.id.OSMMapView), this)
        val searchBar: SearchBar = findViewById(R.id.searchBar)
        val searchView: SearchView = findViewById(R.id.searchView)
        val currentLocationButton: Button = findViewById(R.id.buttonCurrentLocation)

        searchBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logoutButton -> {
                    // Code to be executed when the button is clicked
                    Firebase.auth.signOut()
                    Toast.makeText(this, "Logout Successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AuthenticationActivity::class.java)
                    // to clear activity stack
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    return@setOnMenuItemClickListener true
                }

                else -> {
                    return@setOnMenuItemClickListener false
                }
            }
        }

        searchView.editText.setOnEditorActionListener { v, actionId, event ->
            searchBar.setText(searchView.text)
            searchView.hide()
            // progress bar start

            // synchronized function. it will block processes ( you can run this function on another thread/coroutine)
            val address = OSMMap.search(searchView.text.toString())
            GlobalScope.launch(Dispatchers.Main) {
                if (address != null) {
                    OSMMap.setCenter(address.latitude, address.longitude)
                } else {
                    Toast.makeText(baseContext, "Invalid request", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            //progress bar stop
            false
        }

        currentLocationButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val currentLocation = OSMMap.getLastKnowLocation()
                if (currentLocation != null) {
                    OSMMap.setCenter(currentLocation.latitude, currentLocation.longitude)
                }
            }
        }
//        18.51800310120699, 73.85377755662797
        OSMMap.setMarkerOnPosition(GeoPoint(18.51800310120699, 73.85377755662797))
//         18.518681520319543, 73.85263281356345
        OSMMap.setMarkerOnPosition(GeoPoint(18.518681520319543, 73.85263281356345))
    }
}