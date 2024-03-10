package com.application.parkpilot.activity

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.application.parkpilot.R
import com.application.parkpilot.viewModel.ParkRegisterViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import org.osmdroid.views.MapView
import com.application.parkpilot.AccessHours as DataAccessHours
import com.application.parkpilot.StationAdvance as StationAdvance_DS

class ParkRegisterActivity : AppCompatActivity(R.layout.park_register) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val editTextOpenTime: EditText = findViewById(R.id.editTextOpenTime)
        val editTextCloseTime: EditText = findViewById(R.id.editTextCloseTime)
        val editTextStationName: EditText = findViewById(R.id.editTextStationName)
        val buttonLocationPick: Button = findViewById(R.id.buttonLocationPick)
        val editTextLocation: EditText = findViewById(R.id.editTextLocation)
        val buttonSubmit: Button = findViewById(R.id.buttonSubmit)

        val editTextGettingThere: EditText = findViewById(R.id.editTextGettingThere)
        val editTextStartingPrice: EditText = findViewById(R.id.editTextStartingPrice)


        val imageView1: ImageView = findViewById(R.id.imageView1)
        val imageView2: ImageView = findViewById(R.id.imageView2)
        val imageView3: ImageView = findViewById(R.id.imageView3)

        var imageViewUri1: Uri? = null
        var imageViewUri2: Uri? = null
        var imageViewUri3: Uri? = null

        val layoutLocationPicker = layoutInflater.inflate(R.layout.location_picker, null, false)

        val searchView: SearchView = layoutLocationPicker.findViewById(R.id.searchView)!!
        val searchBar: SearchBar = layoutLocationPicker.findViewById(R.id.searchBar)!!
        val mapView: MapView = layoutLocationPicker.findViewById(R.id.mapViewOSM)!!
        val buttonCurrentLocation: Button =
            layoutLocationPicker.findViewById(R.id.buttonCurrentLocation)!!
        val dialogBox = MaterialAlertDialogBuilder(this).setView(layoutLocationPicker).create()

        val uploadedImages: ArrayList<Uri> = ArrayList()
        var openFlag = false


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

        editTextOpenTime.setOnClickListener {
            openFlag = true
            viewModel.timePicker(supportFragmentManager, "open")
        }

        editTextCloseTime.setOnClickListener {
            viewModel.timePicker(supportFragmentManager, "close")
        }

        var flagImageView1 = false
        imageView1.setOnClickListener {
            if (imageViewUri1 != null) {
                imageView1.load(R.drawable.add_circle_icon)
                imageViewUri1 = null
            } else {
                flagImageView1 = true
                viewModel.imagePicker()
            }
        }

        var flagImageView2 = false
        imageView2.setOnClickListener {
            if (imageViewUri2 != null) {
                imageView2.load(R.drawable.add_circle_icon)
                imageViewUri2 = null
            } else {
                flagImageView2 = true
                viewModel.imagePicker()
            }
        }

        imageView3.setOnClickListener {
            if (imageViewUri3 != null) {
                imageView3.load(R.drawable.add_circle_icon)
                imageViewUri3 = null
            } else {
                viewModel.imagePicker()
            }
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
            viewModel.uploadBasic(
                editTextStationName.text.toString(),
                editTextStartingPrice.text.toString().toInt()
            )
            viewModel.uploadAdvance(
                StationAdvance_DS(
                    getThinkShouldYouKnow(),
                    getAmenities(),
                    editTextGettingThere.text.toString(),
                    getAccessTime()
                )
            )
        }

        viewModel.timePicker.liveDataTimePicker.observe(this) {
            if (openFlag) {
                editTextOpenTime.setText(viewModel.timePicker.format12(it))
                openFlag = false
            } else {
                editTextCloseTime.setText(viewModel.timePicker.format12(it))
            }
        }

        viewModel.photoPicker.pickedImage.observe(this) {
            if (flagImageView1) {
                imageViewUri1 = it
                imageView1.load(it)
                flagImageView1 = false
            } else if (flagImageView2) {
                imageViewUri2 = it
                imageView2.load(it)
                flagImageView2 = false
            } else {
                imageViewUri3 = it
                imageView3.load(it)
            }
        }
    }

    private fun getAmenities(): List<String> {
        val switchValet: MaterialSwitch = findViewById(R.id.switchValet)
        val switchEVCharging: MaterialSwitch = findViewById(R.id.switchEVCharging)
        val selectedAmenities: ArrayList<String> = ArrayList()

        if (switchValet.isChecked) selectedAmenities.add("valet")
        if (switchEVCharging.isChecked) selectedAmenities.add("ev_charging")

        return selectedAmenities
    }

    private fun getAccessTime(): DataAccessHours {
        val editTextOpenTime: EditText = findViewById(R.id.editTextOpenTime)
        val editTextCloseTime: EditText = findViewById(R.id.editTextCloseTime)
        val switchMonday: MaterialSwitch = findViewById(R.id.switchMonday)
        val switchTuesday: MaterialSwitch = findViewById(R.id.switchTuesday)
        val switchWednesday: MaterialSwitch = findViewById(R.id.switchWednesday)
        val switchThursday: MaterialSwitch = findViewById(R.id.switchThursday)
        val switchFriday: MaterialSwitch = findViewById(R.id.switchFriday)
        val switchSaturday: MaterialSwitch = findViewById(R.id.switchSaturday)
        val switchSunday: MaterialSwitch = findViewById(R.id.switchSunday)

        val selectedDays: ArrayList<String> = ArrayList()

        selectedDays.apply {
            if (switchMonday.isChecked) add("monday")
            if (switchTuesday.isChecked) add("tuesday")
            if (switchWednesday.isChecked) add("wednesday")
            if (switchThursday.isChecked) add("thursday")
            if (switchFriday.isChecked) add("friday")
            if (switchSaturday.isChecked) add("saturday")
            if (switchSunday.isChecked) add("sunday")
        }

        return DataAccessHours(
            editTextOpenTime.text.toString(),
            editTextCloseTime.text.toString(),
            selectedDays
        )
    }

    private fun getThinkShouldYouKnow(): List<String> {
        val editTextThinkShouldYouKnow: EditText = findViewById(R.id.editTextThinkShouldYouKnow)
        return editTextThinkShouldYouKnow.text.split("\n")
    }
}