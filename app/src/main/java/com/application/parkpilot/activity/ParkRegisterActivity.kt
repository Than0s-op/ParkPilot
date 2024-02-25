package com.application.parkpilot.activity

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.application.parkpilot.R
import com.application.parkpilot.StationAdvance
import com.application.parkpilot.StationBasic
import com.application.parkpilot.StationAdvance as StationAdvance_DS
import com.application.parkpilot.viewModel.ParkRegisterViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.android.material.switchmaterial.SwitchMaterial
import org.osmdroid.views.MapView

class ParkRegisterActivity : AppCompatActivity(R.layout.park_register) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val editTextStationName: EditText = findViewById(R.id.editTextStationName)
        val buttonLocationPick: Button = findViewById(R.id.buttonLocationPick)
        val editTextLocation: EditText = findViewById(R.id.editTextLocation)
        val buttonSubmit: Button = findViewById(R.id.buttonSubmit)

        val editTextGettingThere: EditText = findViewById(R.id.editTextGettingThere)
        val editTextAccessHours: EditText = findViewById(R.id.editTextAccessHours)


//        val imageView1: ImageView = findViewById(R.id.imageView1)
//        val imageView2: ImageView = findViewById(R.id.imageView2)
//        val imageView3: ImageView = findViewById(R.id.imageView3)
//
//        val imageViewUri1:Uri? = null
//        val imageViewUri2:Uri? = null
//        val imageViewUri3:Uri? = null

        val layoutLocationPicker = layoutInflater.inflate(R.layout.location_picker, null, false)

        val searchView: SearchView = layoutLocationPicker.findViewById(R.id.searchView)!!
        val searchBar: SearchBar = layoutLocationPicker.findViewById(R.id.searchBar)!!
        val mapView: MapView = layoutLocationPicker.findViewById(R.id.mapViewOSM)!!
        val buttonCurrentLocation: Button =
            layoutLocationPicker.findViewById(R.id.buttonCurrentLocation)!!
        val dialogBox = MaterialAlertDialogBuilder(this).setView(layoutLocationPicker).create()

        val uploadedImages: ArrayList<Uri> = ArrayList()


        val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ParkRegisterViewModel(mapView, this@ParkRegisterActivity) as T
            }
        })[ParkRegisterViewModel::class.java]


        buttonLocationPick.setOnClickListener {
            dialogBox.show()
        }

        dialogBox.setOnDismissListener {
            viewModel.fillAddress(editTextLocation)
        }

        // when user will type in search bar and press search(action) button (present on keyboard)
        searchView.editText.setOnEditorActionListener { _, _, _ ->
            // setting the typed text to the search bar
            searchBar.setText(searchView.text)

            // hide the searchView(search suggestion box)
            searchView.hide()

            // creating co-routine scope to run search method
            viewModel.search(searchView.text.toString())
            false
        }

        // when current location button press
        buttonCurrentLocation.setOnClickListener {
            // it will set current location in mapView
            viewModel.getCurrentLocation()
        }

        buttonSubmit.setOnClickListener {
            viewModel.uploadLocation()
            viewModel.uploadBasic(editTextStationName.text.toString())
            viewModel.uploadAdvance(
                StationAdvance_DS(
                    getThinkShouldYouKnow(),
                    getAmenities(),
                    editTextAccessHours.text.toString(),
                    editTextGettingThere.text.toString()
                )
            )
        }
    }

    fun getAmenities(): List<String> {
        val switchValet: SwitchMaterial = findViewById(R.id.switchValet)
        val switchEVCharging: SwitchMaterial = findViewById(R.id.switchEVCharging)
        val selectedAmenities: ArrayList<String> = ArrayList()

        if (switchValet.isChecked) selectedAmenities.add("valet")
        if (switchEVCharging.isChecked) selectedAmenities.add("ev_charging")

        return selectedAmenities
    }

    fun getThinkShouldYouKnow(): List<String> {
        val editTextThinkShouldYouKnow: EditText = findViewById(R.id.editTextThinkShouldYouKnow)
        return editTextThinkShouldYouKnow.text.split("\n")
    }
}