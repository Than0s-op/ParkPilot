package com.application.parkpilot.module

import android.app.Activity
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.TimeZone
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.LocalDate
import java.util.Locale

class DatePicker(startDate: Long, endDate: Long) {

    private val constraintsBuilder = CalendarConstraints.Builder()
        .setStart(startDate)
        .setEnd(endDate)
        .build()

    // just init date picker (not build)
    private var datePicker: MaterialDatePicker.Builder<Long> =
        MaterialDatePicker.Builder.datePicker().setCalendarConstraints(constraintsBuilder)

    // to observe date, hence other classes can access date, when it will change
    var pickedDate = MutableLiveData<Long?>()

    fun show(context: Context, message: String) {

        // set message to the date picker, then build it
        val builtDatePicker = datePicker.setTitleText(message).build()

        // show the date picker
        builtDatePicker.show((context as AppCompatActivity).supportFragmentManager, null)

        // when date picker will gone
        builtDatePicker.addOnPositiveButtonClickListener {
            // date has been selected
            if (builtDatePicker.selection != null) {
                // converting selected date into string and set to the data variable
                pickedDate.value = builtDatePicker.selection
            }
        }
    }

    fun format(date: Long): String {
        // creating a date format
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        return simpleDateFormat.format(date)
    }
}