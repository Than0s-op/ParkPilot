package com.application.parkpilot.viewModel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.parkpilot.Book
import com.application.parkpilot.FreeSpotDetails
import com.application.parkpilot.Time
import com.application.parkpilot.User
import com.application.parkpilot.activity.Feedback
import com.application.parkpilot.module.DatePicker
import com.application.parkpilot.module.QRGenerator
import com.application.parkpilot.module.RazorPay
import com.application.parkpilot.module.TimePicker
import com.application.parkpilot.module.firebase.Storage
import com.application.parkpilot.module.firebase.database.Booking
import com.application.parkpilot.module.firebase.database.FreeSpot
import com.application.parkpilot.module.firebase.database.StationAdvance
import com.application.parkpilot.module.firebase.database.StationBasic
import com.application.parkpilot.module.firebase.database.StationLocation
import com.google.android.gms.location.LocationServices
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import kotlin.properties.Delegates
import com.application.parkpilot.StationAdvance as StationAdvanceDataClass
import com.application.parkpilot.StationBasic as StationBasicDataClass
import com.application.parkpilot.module.firebase.database.Feedback as FS_Feedback

class SpotPreviewViewModel(context: Context) : ViewModel() {

    lateinit var stationUID: String
    var isFree by Delegates.notNull<Boolean>()
    val carouselImages = MutableLiveData<List<Any>>()
    val stationBasicInfo = MutableLiveData<StationBasicDataClass>()
    val stationAdvanceInfo = MutableLiveData<StationAdvanceDataClass>()
    val freeSpotInfo = MutableLiveData<FreeSpotDetails>()
    val bookingPossible = MutableLiveData<Boolean>()
    val ticketId = MutableLiveData<String?>()
    val stationRating = MutableLiveData<Pair<Float, Int>>()
    val liveDataDistance = MutableLiveData<String>()

    var fromDate: Long? = null
    var toDate: Long? = null
    var fromTime: Time? = null
    var toTime: Time? = null
    private var stationLocation: GeoPoint? = null
    private var currentLocation: Location? = null
    val timePicker by lazy { TimePicker("pick the time", TimePicker.CLOCK_12H) }
    val datePicker = Calendar.getInstance().let {
        val startTime = it.timeInMillis
        it.add(Calendar.DAY_OF_MONTH, 30)
        val endTime = it.timeInMillis
        DatePicker(startTime, endTime)
    }

    val razorPay by lazy { RazorPay(context as Activity) }
    val qrGenerator by lazy { QRGenerator(context) }
    private val stationBasic by lazy { StationBasic() }
    private val stationAdvance by lazy { StationAdvance() }
    private val fireStoreStationLocation by lazy { StationLocation() }
    private val booking by lazy { Booking() }
    private val fireStoreFeedback by lazy { FS_Feedback() }

    fun getTimeStamp(time: Time, date: Long): Timestamp {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        calendar.set(Calendar.HOUR, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.add(Calendar.HOUR, time.hours)
        calendar.add(Calendar.MINUTE, time.minute)

        val seconds = calendar.timeInMillis / 1000
        return Timestamp(seconds, 0)
    }

    fun getCurrentTimestamp(): Timestamp {
        return Timestamp.now()
    }

    fun loadDetailScreen(onComplete: () -> Unit) {
        viewModelScope.launch {
            stationLocation = fireStoreStationLocation.locationGet(stationUID)
            stationBasicInfo.value = stationBasic.basicGet(stationUID)
            stationAdvanceInfo.value = stationAdvance.advanceGet(stationUID)
            val feedbacks = fireStoreFeedback.feedGet(stationUID)
            stationRating.value = Pair(feedbacks.map { it.value.rating }.sum(), feedbacks.size)
            carouselImages.value = Storage().parkSpotPhotoGet(stationUID)
            onComplete()
        }
    }

    fun getDistance(context: Context) {
        viewModelScope.launch {
            try {
                println("here location")
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                currentLocation = fusedLocationClient.lastLocation.await()
                stationLocation?.let { station ->
                    currentLocation?.let { current ->
                        liveDataDistance.value =
                            String.format("%.1f", current.distanceTo(Location("").apply {
                                longitude = station.longitude
                                latitude = station.latitude
                            }) / 1000) + "km"
                    }
                }
            } catch (e: Exception) {
                println("exception: ${e.message}")
            }
        }
    }

    fun book(fromTimestamp: Timestamp, toTimestamp: Timestamp) {
        val ticket = Book(fromTimestamp, toTimestamp, stationUID, User.UID)
        viewModelScope.launch {
            val count = booking.getCountBetween(ticket)
            stationBasic.basicGet(stationUID)?.let {
                bookingPossible.value = count < it.reserved!!
            }
        }
    }

    fun generateTicket(fromTimestamp: Timestamp, toTimestamp: Timestamp) {
        val ticket = Book(fromTimestamp, toTimestamp, stationUID, User.UID)
        viewModelScope.launch {
            ticketId.value = booking.bookingSet(ticket)
        }
    }

    fun redirect(context: Context) {
        stationLocation?.let { station ->
            currentLocation?.let { current ->
                val uri =
                    Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${current.latitude},${current.longitude}&destination=${station.latitude},${station.longitude}")

                // Create an Intent to open Google Maps with the specified URI
                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(mapIntent)
            }
        }
    }

    fun getFreeSpotDetails() {
        viewModelScope.launch {
            val freeSpot = FreeSpot()
            freeSpot.getDetails(stationUID).let {
                freeSpotInfo.value = it
                stationLocation = it.location
            }
        }
    }

    fun feedback(context: Context, stationUid: String) {
        context.startActivity(Intent(context, Feedback::class.java).apply {
            putExtra("stationUID", stationUid)
        })
    }
}