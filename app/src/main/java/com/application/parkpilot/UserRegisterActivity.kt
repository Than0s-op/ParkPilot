package com.application.parkpilot

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class UserRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_register)

        val editTextBirthDate: EditText = findViewById(R.id.editTextBirthDate)
        val editTextAge: EditText = findViewById(R.id.editTextAge)
        val imageViewProfilePicture: ImageView = findViewById(R.id.imageViewProfilePicture)

        val datePicker = DatePicker(this)
        val photoPicker = PhotoPicker(this)

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
            }/
        }
    }

    private fun getAge(birthDate: String): Int {
        return 0
    }
}