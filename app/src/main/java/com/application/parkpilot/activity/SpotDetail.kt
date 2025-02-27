package com.application.parkpilot.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.AccessHours
import com.application.parkpilot.R
import com.application.parkpilot.User
import com.application.parkpilot.adapter.recycler.Carousel
import com.application.parkpilot.databinding.BookingBinding
import com.application.parkpilot.databinding.SpotDetailBinding
import com.application.parkpilot.view.DialogQRCode
import com.application.parkpilot.viewModel.SpotPreviewViewModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.razorpay.ExternalWalletListener
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener

class SpotDetail : AppCompatActivity(), PaymentResultWithDataListener,
    ExternalWalletListener {

    // late init view model property
    private lateinit var viewModel: SpotPreviewViewModel

    //    private lateinit var buttonBookNow: Button
    private lateinit var binding: SpotDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutBooking: BookingBinding = BookingBinding.inflate(layoutInflater)
        val dialogInflater = MaterialAlertDialogBuilder(this).setView(layoutBooking.root).create()

        binding = SpotDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // getting authentication view model reference [init]
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SpotPreviewViewModel(this@SpotDetail) as T
            }
        })[SpotPreviewViewModel::class.java]

        viewModel.stationUID = intent.getStringExtra("stationUID")!!
        viewModel.isFree = intent.getBooleanExtra("isFree", false)

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuButtonReview -> {
                    viewModel.feedback(this, viewModel.stationUID)
                    true
                }

                R.id.menuButtonNavigate -> {
                    viewModel.redirect(this)
                    true
                }

                else -> false
            }
        }

        if (!viewModel.isFree) {
            viewModel.loadDetailScreen {
                hideShimmer()
            }
        } else {
            viewModel.getFreeSpotDetails()
        }

        setVisibility()

        // loading recycler view default (init) properties
        binding.recycleView.apply {
            layoutManager = CarouselLayoutManager()
        }

        binding.buttonBookNow.setOnClickListener {
            if (User.type == User.ANONYMOUS) {
                startActivity(Intent(this, Authentication::class.java))
            } else {
                dialogInflater.show()
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }


        layoutBooking.buttonBookNow.setOnClickListener {
            if (viewModel.fromTime == null || viewModel.fromDate == null || viewModel.toTime == null || viewModel.toDate == null) {
                Toast.makeText(this, "Please fill the form", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val fromTimestamp = viewModel.getTimeStamp(viewModel.fromTime!!, viewModel.fromDate!!)
            val toTimestamp = viewModel.getTimeStamp(viewModel.toTime!!, viewModel.toDate!!)
            if (toTimestamp < fromTimestamp || fromTimestamp < viewModel.getCurrentTimestamp()) {
                Toast.makeText(this, "Enter valid date and time", Toast.LENGTH_LONG).show()
            } else {
                viewModel.book(fromTimestamp, toTimestamp)
            }
            dialogInflater.dismiss()
        }

        var flagDate = false
        var flagTime = false

        layoutBooking.editTextFromDate.setOnClickListener {
            flagDate = true
            viewModel.datePicker.show(this, "Select From Date")
        }

        layoutBooking.editTextToDate.setOnClickListener {
            flagDate = false
            viewModel.datePicker.show(this, "Select To Date")
        }

        layoutBooking.editTextFromTime.setOnClickListener {
            flagTime = true
            viewModel.timePicker.showTimePicker(supportFragmentManager, null)
        }

        layoutBooking.editTextToTime.setOnClickListener {
            flagTime = false
            viewModel.timePicker.showTimePicker(supportFragmentManager, null)
        }


        viewModel.carouselImages.observe(this) { images ->
            binding.recycleView.adapter = Carousel(this, R.layout.round_carousel, images)
            viewModel.getDistance(this)
        }

        viewModel.stationAdvanceInfo.observe(this) {
            // loading "Think should you know" section
            binding.textViewPolicies.text = it.policies

            // loading amenities section
            loadAmenities(it.amenities)

            // loading access hours section
            loadAccessHours(it.accessHours)
//            textViewAccessHours.text = it.accessHours
        }

        viewModel.freeSpotInfo.observe(this) {
            binding.textViewName.text = it.landMark
            binding.recycleView.adapter = Carousel(this, R.layout.round_carousel, it.images)
            binding.textViewPolicies.text = it.policies
            viewModel.getDistance(this)
            hideShimmer()
        }

        viewModel.stationBasicInfo.observe(this) {
            binding.textViewName.text = it.name
            binding.textViewPrice.text = it.price.toString()
        }

        viewModel.stationRating.observe(this) {
            if (it.second != 0) {
                binding.textViewRating.text = String.format("%.1f", it.first / it.second)
                binding.textViewRating.backgroundTintList = getTint(it.first / it.second)
                binding.textViewNumberOfUser.text = it.second.toString()
            } else {
                binding.textViewRating.text = "0.0"
                binding.textViewRating.backgroundTintList = getTint(0.0f)
                binding.textViewNumberOfUser.text = "0"
            }
        }

        viewModel.datePicker.pickedDate.observe(this) {
            it?.let {
                val date = viewModel.datePicker.format(it)
                if (flagDate) {
                    layoutBooking.editTextFromDate.setText(date)
                    viewModel.fromDate = it
                } else {
                    layoutBooking.editTextToDate.setText(date)
                    viewModel.toDate = it
                }
                flagDate = false
            }
        }

        viewModel.timePicker.liveDataTimePicker.observe(this) {
            if (flagTime) {
                layoutBooking.editTextFromTime.setText(viewModel.timePicker.format12(it))
                viewModel.fromTime = it
            } else {
                layoutBooking.editTextToTime.setText(viewModel.timePicker.format12(it))
                viewModel.toTime = it
            }
            flagTime = false
        }

        viewModel.bookingPossible.observe(this) { isPossible ->
            if (isPossible) {
                viewModel.razorPay.makePayment(
                    viewModel.razorPay.INDIA,
                    binding.textViewPrice.text.toString().toInt(),
                    "123"
                )
            } else {
                Toast.makeText(this, "Spot not available", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.ticketId.observe(this) { ticketID ->
            ticketID?.let {
                val qrcode = viewModel.qrGenerator.generate(ticketID)
                MaterialAlertDialogBuilder(this)
                    .setView(DialogQRCode(this, qrcode, "this you qr").layout).show()
            }
        }

        viewModel.liveDataDistance.observe(this) {
            binding.textViewDistance.text = it
        }
    }

    private fun hideShimmer() {
        binding.shimmerEffect.visibility = View.GONE
        binding.scrollView.visibility = View.VISIBLE
    }

    private fun loadAccessHours(accessHours: AccessHours) {
        val tint = ColorStateList.valueOf(getColor(R.color.black))
        val textColor = ColorStateList.valueOf(getColor(R.color.white))
        for (day in accessHours.selectedDays) {
            when (day) {
                getString(R.string.monday) -> findViewById<TextView>(R.id.textViewMonday).apply {
                    backgroundTintList = tint
                    setTextColor(textColor)
                }

                getString(R.string.tuesday) -> findViewById<TextView>(R.id.textViewTuesday).apply {
                    backgroundTintList = tint
                    setTextColor(textColor)
                }

                getString(R.string.wednesday) -> findViewById<TextView>(R.id.textViewWednesday).apply {
                    backgroundTintList = tint
                    setTextColor(textColor)
                }

                getString(R.string.thursday) -> findViewById<TextView>(R.id.textViewThursday).apply {
                    backgroundTintList = tint
                    setTextColor(textColor)
                }

                getString(R.string.friday) -> findViewById<TextView>(R.id.textViewFriday).apply {
                    backgroundTintList = tint
                    setTextColor(textColor)
                }

                getString(R.string.saturday) -> findViewById<TextView>(R.id.textViewSaturday).apply {
                    backgroundTintList = tint
                    setTextColor(textColor)
                }

                getString(R.string.sunday) -> findViewById<TextView>(R.id.textViewSunday).apply {
                    backgroundTintList = tint
                }
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

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        val fromTimestamp = viewModel.getTimeStamp(viewModel.fromTime!!, viewModel.fromDate!!)
        val toTimestamp = viewModel.getTimeStamp(viewModel.toTime!!, viewModel.toDate!!)
        viewModel.generateTicket(fromTimestamp, toTimestamp)
        Toast.makeText(this, "Payment successfully done", Toast.LENGTH_LONG).show()
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Toast.makeText(this, "Payment failed", Toast.LENGTH_LONG).show()
    }

    override fun onExternalWalletSelected(p0: String?, p1: PaymentData?) {

    }

    private fun setVisibility() {
        if (viewModel.isFree) {
            binding.priceLayout.visibility = View.GONE
            binding.textViewRating.visibility = View.GONE
            binding.textViewNumberOfUser.visibility = View.GONE
            binding.amenitiesCard.visibility = View.GONE
            binding.accessHoursCard.visibility = View.GONE
            binding.buttonBookNow.visibility = View.GONE
            binding.freeImageView.visibility = View.VISIBLE
            // feedback
            binding.bottomAppBar.menu.getItem(0).setVisible(false)
        }
    }
}