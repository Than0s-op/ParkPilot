package com.application.parkpilot.activity

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.size.Scale
import com.application.parkpilot.R
import com.application.parkpilot.UserCollection
import com.application.parkpilot.UserProfile
import com.application.parkpilot.module.DatePicker
import com.application.parkpilot.module.PhotoPicker
import com.application.parkpilot.module.firebase.FireStore
import com.application.parkpilot.module.firebase.Storage
import com.google.android.material.button.MaterialButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class UserRegisterActivity : AppCompatActivity(R.layout.user_register) {
    lateinit var user:FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //
        val imageViewProfilePicture: ImageView = findViewById(R.id.imageViewProfilePicture)
        val editTextUserName: EditText = findViewById(R.id.editTextUserName)
        val editTextFirstName: EditText = findViewById(R.id.editTextFirstName)
        val editTextLastName: EditText = findViewById(R.id.editTextLastName)
        val editTextBirthDate: EditText = findViewById(R.id.editTextBirthDate)
        val editTextAge: EditText = findViewById(R.id.editTextAge)
        val radioGroupGender: RadioGroup = findViewById(R.id.radioGroupGender)
        val buttonSave: Button = findViewById(R.id.buttonSave)
        val editTextPhoneNumber:EditText = findViewById(R.id.editTextPhoneNumber)
        val editTextEmail:EditText = findViewById(R.id.editTextEmail)
        val buttonVerifyPhoneNumber:Button = findViewById(R.id.buttonVerifyPhoneNumber)
        val buttonVerifyEmail:Button = findViewById(R.id.buttonVerifyEmail)

        //
        user = Firebase.auth.currentUser!!
        val fireStore = FireStore()
        val datePicker = DatePicker(this)
        val photoPicker = PhotoPicker(this)

        //
        var photoUrl = user.photoUrl

        // does user have user name?,yes it means she has profile name and image
        if (user.displayName != null) {
            CoroutineScope(Dispatchers.Main).launch {
                val profileImage = uriImageLoader(photoUrl!!)
                imageViewProfilePicture.setImageDrawable(profileImage.drawable)
            }

            // setting user name to edit text
            editTextUserName.setText(user.displayName)
        }
//
        CoroutineScope(Dispatchers.Main).launch {
            fireStore.userGet(user.uid)?.let{
                editTextFirstName.setText(it.firstName)
                editTextLastName.setText(it.lastName)
                editTextBirthDate.setText(it.birthDate)
                editTextAge.setText(getAge(it.birthDate))
                radioGroupGender.check(if(it.gender == "female") R.id.radioButtonFemale else R.id.radioButtonMale)
            }
        }

//
//        if (editTextPhoneNumber.text.isNotEmpty()) {
//            editTextPhoneNumber.isEnabled = false
//            buttonVerifyPhoneNumber.isEnabled = false
//            buttonVerifyPhoneNumber.text = "Verified"
//            buttonVerifyPhoneNumber.icon =
//                AppCompatResources.getDrawable(this, R.drawable.check_icon)
//        }
//
//        if (editTextEmail.text.isNotEmpty()) {
//            editTextEmail.isEnabled = false
//            buttonVerifyEmail.isEnabled = false
//            buttonVerifyEmail.text = "Verified"
//            buttonVerifyEmail.icon = AppCompatResources.getDrawable(this, R.drawable.check_icon)
//        }
//
//
//
        editTextBirthDate.setOnClickListener {
            // start and end dates format should be yyyy-mm-dd (modify this function)
            datePicker.showDatePicker("Select Birth Date", null, null)
        }
//
        imageViewProfilePicture.setOnClickListener {
            photoPicker.showPhotoPicker()
        }
//
//        buttonVerifyEmail.setOnClickListener {
//            CompanionObjects.currentUser!!.verifyBeforeUpdateEmail(editTextEmail.text.toString())
//                .addOnSuccessListener {
//                    println("success")
//                }.addOnCompleteListener {
//                    println("complete")
//                }.addOnFailureListener {
//                    println(it.localizedMessage)
//                }
//        }
//
        buttonSave.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                var result = true

                result = fireStore.userPut(
                    UserCollection(
                        editTextFirstName.text.toString(),
                        editTextLastName.text.toString(),
                        editTextBirthDate.text.toString(),
                        if (radioGroupGender.checkedRadioButtonId == R.id.radioButtonFemale) "female" else "male"
                    ), user.uid
                ) and result

                val storage = Storage()
                val storageProfilePhotoUri = storage.userProfilePhotoPut(user.uid, photoUrl!!)


                result =
                    updateProfile(
                        UserProfile(
                            editTextUserName.text.toString(),
                            storageProfilePhotoUri
                        )
                    ) and result

                if (result) {
                    Toast.makeText(
                        this@UserRegisterActivity,
                        "Information Save Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@UserRegisterActivity,
                        "Failed Save Information",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
//
        datePicker.pickedDate.observe(this) {
            if (datePicker.pickedDate.value != null) {
                editTextBirthDate.setText(datePicker.pickedDate.value)
                editTextAge.setText(getAge(datePicker.pickedDate.value!!))
            }
        }

        photoPicker.pickedImage.observe(this) {
            if (photoPicker.pickedImage.value != null) {
                photoUrl = photoPicker.pickedImage.value
                CoroutineScope(Dispatchers.Main).launch {
                    val profileImage = uriImageLoader(photoUrl!!)
                    imageViewProfilePicture.setImageDrawable(profileImage.drawable)
                }
            }
        }
    }

    // format should be in day,month,year
    private fun getAge(birthDate: String): String {
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
        return age.toString()
    }

    private suspend fun updateProfile(data: UserProfile): Boolean {
        val profileUpdates = userProfileChangeRequest {
            displayName = data.displayName.trim()
            photoUri = data.photoUri
        }
        var result = false

        // To update profile
        user.updateProfile(profileUpdates).addOnSuccessListener {
            result = true
        }.await()

        return result
    }

    private suspend fun uriImageLoader(
        photoUrl: Uri,
        width: Int = 192,
        height: Int = 192
    ): ImageResult {
        // request for profile image of user
        val profileImageRequest = ImageRequest.Builder(this)
            .data(photoUrl)
            .size(width, height)
            .scale(Scale.FIT)
            .build()

        return imageLoader.execute(profileImageRequest)
    }
}