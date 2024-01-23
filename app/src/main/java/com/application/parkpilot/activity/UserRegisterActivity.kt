package com.application.parkpilot.activity

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.application.parkpilot.CompanionObjects
import com.application.parkpilot.R
import com.application.parkpilot.module.DatePicker
import com.application.parkpilot.module.PhotoPicker
import java.time.LocalDate

class UserRegisterActivity : AppCompatActivity() {

    private lateinit var editTextPhoneNumber: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var buttonVerifyPhoneNumber: com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
    private lateinit var buttonVerifyEmail: com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_register)

        //
        val imageViewProfilePicture: ImageView = findViewById(R.id.imageViewProfilePicture)
        val editTextUserName: EditText = findViewById(R.id.editTextUserName)
        val editTextFirstName: EditText = findViewById(R.id.editTextFirstName)
        val editTextLastName: EditText = findViewById(R.id.editTextLastName)
        val editTextBirthDate: EditText = findViewById(R.id.editTextBirthDate)
        val editTextAge: EditText = findViewById(R.id.editTextAge)
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber)
        editTextEmail = findViewById(R.id.editTextEmail)
        buttonVerifyPhoneNumber = findViewById(R.id.buttonVerifyPhoneNumber)
        buttonVerifyEmail = findViewById(R.id.buttonVerifyEmail)

        //
        imageViewProfilePicture.setImageURI(CompanionObjects.currentUser?.photoUrl)
        editTextUserName.setText(CompanionObjects.currentUser?.displayName)
        editTextPhoneNumber.setText(CompanionObjects.currentUser?.phoneNumber)
        editTextEmail.setText(CompanionObjects.currentUser?.email)

        if (editTextPhoneNumber.text.isNotEmpty()) {
            editTextPhoneNumber.isEnabled = false
            buttonVerifyPhoneNumber.isEnabled = false
            buttonVerifyPhoneNumber.text = "Verified"
            buttonVerifyPhoneNumber.icon = AppCompatResources.getDrawable(this,R.drawable.check_icon)
        }

        if (editTextEmail.text.isNotEmpty()) {
            editTextEmail.isEnabled = false
            buttonVerifyEmail.isEnabled = false
            buttonVerifyEmail.text = "Verified"
            buttonVerifyEmail.icon = AppCompatResources.getDrawable(this,R.drawable.check_icon)
        }

        val datePicker = DatePicker(this)
        val photoPicker = PhotoPicker(this)


        editTextBirthDate.setOnClickListener {
            // start and end dates format should be yyyy-mm-dd (modify this function)
            datePicker.showDatePicker("Select Birth Date", null, null)
        }

        imageViewProfilePicture.setOnClickListener {
            photoPicker.showPhotoPicker()
        }

        buttonVerifyEmail.setOnClickListener {
            CompanionObjects.currentUser!!.verifyBeforeUpdateEmail(editTextEmail.text.toString())
                .addOnSuccessListener {
                    println("success")
                }.addOnCompleteListener {
                println("complete")
            }.addOnFailureListener {
                println(it.localizedMessage)
            }
        }

        datePicker.pickedDate.observe(this) {
            if (datePicker.pickedDate.value != null) {
                editTextBirthDate.setText(datePicker.pickedDate.value)
                editTextAge.setText(getAge(datePicker.pickedDate.value!!).toString())
            }
        }

        photoPicker.pickedImage.observe(this) {
            if (photoPicker.pickedImage.value != null) {
                imageViewProfilePicture.setImageURI(photoPicker.pickedImage.value)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (CompanionObjects.currentUser?.isEmailVerified == true) {
            editTextEmail.isEnabled = false
            buttonVerifyEmail.isEnabled = false
            buttonVerifyEmail.icon = getDrawable(R.drawable.check_icon)
            buttonVerifyEmail.text = "Verified"
        }
    }

    private fun getAge(birthDate: String): Int {
        // getting today's date
        val current = LocalDate.now()

        // parsing the "birthDate" string to get birth (day, month, year)
        val birthYear = birthDate.substring(6).toInt()
        val birthMonth = birthDate.substring(3, 5).toInt()
        val birthDay = birthDate.substring(0, 2).toInt()

        // finding the age of the user ( "-1"  to handel current year)
        var age = current.year - birthYear - 1

        // to check user birthDay has gone or not in current year. if yes increment age by 1
        if (birthMonth < current.monthValue || (birthMonth == current.monthValue && birthDay <= current.dayOfMonth)) age++

        // return the age
        return age
    }
}