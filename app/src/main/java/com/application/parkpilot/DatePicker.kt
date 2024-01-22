package com.application.parkpilot

import android.icu.util.Calendar
import android.icu.util.TimeZone
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.LocalDate

class DatePicker(private val activity: AppCompatActivity) {

    // just init date picker (not build)
    private var datePicker: MaterialDatePicker.Builder<Long> = MaterialDatePicker.Builder
        .datePicker()
        .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)

    // below function will take string(date) as input and convert it into millis(Long)
    private fun dateToMillis(date:String):Long{
        // date format should be in yyyy-mm-dd
        val localDate = LocalDate.parse(date)
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        // set date to the calendar, here monthValue is reduce by 1 to handel a case
        calendar.set(localDate.year,localDate.monthValue-1,localDate.dayOfMonth)

        // return date as millis
        return calendar.timeInMillis
    }
    fun showDatePicker(message: String, start: String?, end: String?) {
        // creating the copy above datePicker
        val datePicker = datePicker

        // if start and end have "date" (both required) then, set calendar constraints
        if (start != null && end != null) {
            // building calendar constraint
            val constraintsBuilder = CalendarConstraints.Builder()
                .setStart(dateToMillis(start))
                .setEnd(dateToMillis(end))
            // setting up calendar constraints to [***Local*** date picker]
            datePicker.setCalendarConstraints(constraintsBuilder.build())
        }

        // set message to the date picker, then build it and show
        datePicker.setTitleText(message)
            .build()
            .show(activity.supportFragmentManager, null)
    }
}