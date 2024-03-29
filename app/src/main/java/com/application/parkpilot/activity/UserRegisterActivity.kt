package com.application.parkpilot.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.application.parkpilot.R
import com.application.parkpilot.UserCollection
import com.application.parkpilot.UserProfile
import com.application.parkpilot.viewModel.UserRegisterViewModel
import com.google.firebase.auth.FirebaseUser

class UserRegisterActivity : AppCompatActivity(R.layout.user_register) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init view
        val imageViewProfilePicture: ImageView = findViewById(R.id.imageViewProfilePicture)
        val editTextUserName: EditText = findViewById(R.id.editTextUserName)
        val editTextFirstName: EditText = findViewById(R.id.editTextFirstName)
        val editTextLastName: EditText = findViewById(R.id.editTextLastName)
        val editTextBirthDate: EditText = findViewById(R.id.editTextBirthDate)
        val editTextAge: EditText = findViewById(R.id.editTextAge)
        val radioGroupGender: RadioGroup = findViewById(R.id.radioGroupGender)
        val buttonSave: Button = findViewById(R.id.buttonSave)
        val editTextPhoneNumber: EditText = findViewById(R.id.editTextPhoneNumber)
        val editTextEmail: EditText = findViewById(R.id.editTextEmail)
        val buttonVerifyPhoneNumber: Button = findViewById(R.id.buttonVerifyPhoneNumber)
        val buttonVerifyEmail: Button = findViewById(R.id.buttonVerifyEmail)

        // getting userRegister view model reference
        val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UserRegisterViewModel(this@UserRegisterActivity) as T
            }
        })[UserRegisterViewModel::class.java]

        // it will store MainActivity intent or null
        // why it's here? ans:- [ if user came from Main Activity then we have to throw user again to Main Activity, otherwise do nothing]
        var nextIntent: Intent? = Intent(this, MainActivity::class.java)

        viewModel.getProfileDetails()

        // get user details from user collection
        viewModel.getUserDetails()

        editTextBirthDate.setOnClickListener {
            // start and end dates format should be yyyy-mm-dd (modify this function)
            viewModel.datePicker.showDatePicker("Select Birth Date", null, null)
        }

        imageViewProfilePicture.setOnClickListener {
            // start photo picker
            viewModel.photoPicker.showPhotoPicker()
        }

        buttonSave.setOnClickListener {
            // uploading the user data
            viewModel.saveUserData(
                this,
                UserCollection(
                    editTextFirstName.text.toString(),
                    editTextLastName.text.toString(),
                    editTextBirthDate.text.toString(),
                    if (radioGroupGender.checkedRadioButtonId == R.id.radioButtonFemale) "female" else "male"
                ),
                UserProfile(
                    editTextUserName.text.toString()
                )
            )
        }

        // it is a observer of getImage method's result
        viewModel.imageLoaderResult.observe(this) { image ->
            // loaded image applying to profile picture
            imageViewProfilePicture.setImageDrawable(image.drawable)
        }

        // it will execute when fireStore result get successfully
        viewModel.userInformation.observe(this) { userCollection ->
            // set the data if user Collection is not null
            userCollection?.let {
                editTextFirstName.setText(it.firstName)
                editTextLastName.setText(it.lastName)
                editTextBirthDate.setText(it.birthDate)
                editTextAge.setText(viewModel.getAge(it.birthDate))
                radioGroupGender.check(if (it.gender == "female") R.id.radioButtonFemale else R.id.radioButtonMale)
                // if user came from home/other activity except mainActivity
                nextIntent = null
            }
        }

        viewModel.userProfile.observe(this) { userProfile ->
            userProfile?.let {
                editTextUserName.setText(userProfile.userName)
                if (userProfile.userPicture != null) {
                    imageViewProfilePicture.load(userProfile.userPicture)
                    viewModel.photoUrl = userProfile.userPicture
                } else {
                    imageViewProfilePicture.load(R.drawable.person_icon)
                }
            }
        }

        // it will execute when date picker get some date from user
        viewModel.datePicker.pickedDate.observe(this) { date ->
            // set date to birthdate editText if is not null
            date?.let {
                editTextBirthDate.setText(it)
                editTextAge.setText(viewModel.getAge(it))
            }
        }

        // it will execute when photo picker get image
        viewModel.photoPicker.pickedImage.observe(this) { imageUri ->
            // execute below code if imageUri is not null
            imageUri?.let {
                viewModel.photoUrl = it
                imageViewProfilePicture.setImageURI(it)
            }
        }

        // it will execute when user data uploaded successfully or failed to upload
        viewModel.isUploaded.observe(this) { isUploaded ->
            if (isUploaded) {
                Toast.makeText(
                    this, "Information Save Successfully", Toast.LENGTH_SHORT
                ).show()

                nextIntent?.let {
                    startActivity(nextIntent)
                }
                finish()
            } else {
                Toast.makeText(
                    this, "Failed Save Information", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}