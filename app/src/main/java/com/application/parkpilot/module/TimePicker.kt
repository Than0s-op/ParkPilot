package com.application.parkpilot.module

import androidx.fragment.app.FragmentManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class TimePicker {
    fun showTimePicker(manager: FragmentManager, message:String,timeFormat:Int){
        // for 12 = TimeFormat.CLOCK_12H
        // for 24 = TimeFormat.CLOCK_24H

        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(timeFormat)
                .setTitleText(message)
                .build()
        timePicker.show(manager,null)

        timePicker.addOnPositiveButtonClickListener{
            // implementation is incomplete
        }
    }
}