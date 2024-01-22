package com.application.parkpilot.activity

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.application.parkpilot.R
import com.application.parkpilot.module.DatePicker
import com.application.parkpilot.module.PhotoPicker
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.time.LocalDate

class UserRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_register)

        val editTextBirthDate: EditText = findViewById(R.id.editTextBirthDate)
        val editTextAge: EditText = findViewById(R.id.editTextAge)
        val imageViewProfilePicture: ImageView = findViewById(R.id.imageViewProfilePicture)

        val datePicker = DatePicker(this)
        val photoPicker = PhotoPicker(this)
        val user = Firebase.auth.currentUser


        editTextBirthDate.setOnClickListener {
            // date format should be yyyy-mm-dd
            datePicker.showDatePicker("Select Birth Date", "2020-11-20", "2021-11-18")
        }

        imageViewProfilePicture.setOnClickListener {
            photoPicker.showPhotoPicker()
        }

        datePicker.pickedDate.observe(this) {
            if (datePicker.pickedDate.value != null) {
                editTextBirthDate.setText(datePicker.pickedDate.value)
                editTextAge.setText(getAge(datePicker.pickedDate.value!!).toString())
            }
        }

        photoPicker.pickedImage.observe(this){
            if(photoPicker.pickedImage.value != null){
                imageViewProfilePicture.setImageURI(photoPicker.pickedImage.value)
            }
        }
    }

    private fun getAge(birthDate: String): Int {
        val current = LocalDate.now()
        val birthYear = birthDate.substring(6).toInt()
        val birthMonth = birthDate.substring(3,5).toInt()
        val birthDay = birthDate.substring(0,2).toInt()
        var age = current.year - birthYear - 1;
        if(birthMonth < current.monthValue || (birthMonth == current.monthValue && birthDay <= current.dayOfMonth)) age++
        return age
    }
}