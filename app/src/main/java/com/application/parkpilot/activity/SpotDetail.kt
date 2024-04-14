package com.application.parkpilot.activity

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.AccessHours
import com.application.parkpilot.R
import com.application.parkpilot.adapter.CarouselRecyclerView
import com.application.parkpilot.viewModel.SpotPreviewViewModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class SpotDetail : AppCompatActivity(R.layout.spot_detail) {

    // late init view model property
    private lateinit var viewModel: SpotPreviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // view init
        val recyclerView: RecyclerView = findViewById(R.id.recycleView)
        val textViewName: TextView = findViewById(R.id.textViewName)
        val textViewRating: TextView = findViewById(R.id.textViewRating)
        val textViewDistance: TextView = findViewById(R.id.textViewDistance)
        val textViewPrice: TextView = findViewById(R.id.textViewPrice)
        val textViewPolicies: TextView = findViewById(R.id.textViewPolicies)
        val buttonFeedback: ExtendedFloatingActionButton = findViewById(R.id.buttonFeedback)
        val textViewNumberOfUser: TextView = findViewById(R.id.textViewNumberOfUser)

        val viewModel = ViewModelProvider(this)[SpotPreviewViewModel::class.java]
        val stationUID = intent.getStringExtra("stationUID")!!

        viewModel.loadAdvanceInfo(stationUID)
        viewModel.loadBasicInfo(stationUID)
        viewModel.loadRating(stationUID)

        // loading recycler view default (init) properties
        recyclerView.apply {
            layoutManager = CarouselLayoutManager()
            viewModel.loadCarousel(stationUID)
        }

        buttonFeedback.setOnClickListener {
            viewModel.feedback(this, stationUID)
        }

        viewModel.carouselImages.observe(this) { images ->
            recyclerView.adapter = CarouselRecyclerView(this, R.layout.square_carousel, images)
        }

        viewModel.stationAdvanceInfo.observe(this) {
            // loading "Think should you know" section
            textViewPolicies.text = it.policies

            // loading amenities section
            loadAmenities(it.amenities)

            // loading access hours section
            loadAccessHours(it.accessHours)
//            textViewAccessHours.text = it.accessHours
        }

        viewModel.stationBasicInfo.observe(this) {
            textViewName.text = it.name
            textViewPrice.text = it.price.toString()
        }

        viewModel.stationRating.observe(this) {
            textViewRating.text = String.format("%.1f", it.first / it.second)
            textViewRating.backgroundTintList = getTint(it.first / it.second)
            textViewNumberOfUser.text = it.second.toString()
        }

        viewModel.stationLocation.observe(this) {
//            textViewDistance.text =
        }
    }

    private fun loadAccessHours(accessHours: AccessHours) {
        val tint = ColorStateList.valueOf(getColor(R.color.md_theme_dark_onSecondaryContainer))
        for (day in accessHours.selectedDays) {
            when (day) {
                getString(R.string.monday) -> findViewById<TextView>(R.id.textViewMonday).backgroundTintList =
                    tint

                getString(R.string.tuesday) -> findViewById<TextView>(R.id.textViewTuesday).backgroundTintList =
                    tint

                getString(R.string.wednesday) -> findViewById<TextView>(R.id.textViewWednesday).backgroundTintList =
                    tint

                getString(R.string.thursday) -> findViewById<TextView>(R.id.textViewThursday).backgroundTintList =
                    tint

                getString(R.string.friday) -> findViewById<TextView>(R.id.textViewFriday).backgroundTintList =
                    tint

                getString(R.string.saturday) -> findViewById<TextView>(R.id.textViewSaturday).backgroundTintList =
                    tint

                getString(R.string.sunday) -> findViewById<TextView>(R.id.textViewSunday).backgroundTintList =
                    tint
            }
        }
        val editTextOpenTime: EditText = findViewById(R.id.editTextOpenTime)
        val editTextCloseTime: EditText = findViewById(R.id.editTextCloseTime)
        editTextOpenTime.setText(accessHours.open)
        editTextCloseTime.setText(accessHours.close)
    }

    private fun loadAmenities(amenitiesList: List<String>) {
        for (amenities in amenitiesList) {
            when (amenities) {
                getString(R.string.ev_charging) -> findViewById<TextView>(R.id.textViewEV).visibility =
                    View.VISIBLE

                getString(R.string.valet) -> findViewById<TextView>(R.id.textViewValet).visibility =
                    View.VISIBLE

                getString(R.string.garage) -> findViewById<TextView>(R.id.textViewGarage).visibility =
                    View.VISIBLE

                getString(R.string.on_site_staff) -> findViewById<TextView>(R.id.textViewStaff).visibility =
                    View.VISIBLE

                getString(R.string.wheelchair_accessible) -> findViewById<TextView>(R.id.textViewWheelchair).visibility =
                    View.VISIBLE
            }
        }
    }

    private fun getTint(ratting: Float): ColorStateList {
        return if (ratting <= 2.5) {
            ColorStateList.valueOf(Color.parseColor("#e5391a"))
        } else if (ratting < 4) {
            ColorStateList.valueOf(Color.parseColor("#cb8300"))
        } else {
            ColorStateList.valueOf(Color.parseColor("#026a28"))
        }
    }
}