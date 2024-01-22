package com.application.parkpilot

import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

class UserRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_register)

        val editTextBirthDate: EditText = findViewById(R.id.editTextBirthDate)
        editTextBirthDate.setOnClickListener {
            val datePicker = DatePicker(this)
            // date format should be yyyy-mm-dd
            datePicker.showDatePicker("Select Birth Date","2020-11-20","2021-11-18")
        }
    }
}