package com.application.parkpilot.activity

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.application.parkpilot.R
import com.application.parkpilot.UserCollection
import com.application.parkpilot.UserProfile
import com.application.parkpilot.databinding.UserRegisterBinding
import com.application.parkpilot.viewModel.UserRegisterViewModel

class UserRegister : AppCompatActivity(R.layout.user_register) {
    private lateinit var binding: UserRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init view
        binding = UserRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // getting authentication view model reference [init]
        val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UserRegisterViewModel(this@UserRegister) as T
            }
        })[UserRegisterViewModel::class.java]

        viewModel.getProfileDetails()

        // get user details from user collection
        viewModel.getUserDetails()

        binding.editTextBirthDate.setOnClickListener {
            // start and end dates format should be yyyy-mm-dd (modify this function)
            viewModel.datePicker.showDatePicker(this, "Select Birth Date")
        }

        binding.imageViewProfilePicture.setOnClickListener {
            // start photo picker
            viewModel.photoPicker.showPhotoPicker()
        }

        binding.buttonSave.setOnClickListener {
            var isValid = true
            isValid = isValid(binding.editTextUserName) and isValid
            isValid = isValid(binding.editTextFirstName) and isValid
            isValid = isValid(binding.editTextLastName) and isValid
            isValid = isValid(binding.editTextBirthDate) and isValid

            if (isValid) {
                showProgress()

                // uploading the user data
                viewModel.saveUserData(
                    this,
                    UserCollection(
                        binding.editTextFirstName.text.toString(),
                        binding.editTextLastName.text.toString(),
                        binding.editTextBirthDate.text.toString(),
                        if (binding.radioGroupGender.checkedRadioButtonId == R.id.radioButtonFemale) "female" else "male"
                    ),
                    UserProfile(
                        binding.editTextUserName.text.toString()
                    )
                )
            }
        }

        // it is a observer of getImage method's result
        viewModel.imageLoaderResult.observe(this) { image ->
            // loaded image applying to profile picture
            binding.imageViewProfilePicture.setImageDrawable(image.drawable)
        }

        // it will execute when fireStore result get successfully
        viewModel.userInformation.observe(this) { userCollection ->
            // set the data if user Collection is not null
            userCollection?.let {
                binding.editTextFirstName.setText(it.firstName)
                binding.editTextLastName.setText(it.lastName)
                binding.editTextBirthDate.setText(it.birthDate)
                binding.editTextAge.setText(viewModel.getAge(it.birthDate))
                binding.radioGroupGender.check(if (it.gender == "female") R.id.radioButtonFemale else R.id.radioButtonMale)
            }
        }

        viewModel.userProfile.observe(this) { userProfile ->
            userProfile?.let {
                binding.editTextUserName.setText(userProfile.userName)
                if (userProfile.userPicture != null) {
                    binding.imageViewProfilePicture.load(userProfile.userPicture)
                    viewModel.photoUrl = userProfile.userPicture
                } else {
                    binding.imageViewProfilePicture.load(R.drawable.person_icon)
                }
            }
        }

        // it will execute when date picker get some date from user
        viewModel.datePicker.pickedDate.observe(this) { date ->
            // set date to birthdate editText if is not null
            date?.let {
                val date = viewModel.datePicker.format(it)
                binding.editTextBirthDate.setText(date)
                binding.editTextAge.setText(viewModel.getAge(date))
            }
        }

        // it will execute when photo picker get image
        viewModel.photoPicker.pickedImage.observe(this) { imageUri ->
            // execute below code if imageUri is not null
            imageUri?.let {
                viewModel.photoUrl = it
                binding.imageViewProfilePicture.setImageURI(it)
            }
        }

        // it will execute when user data uploaded successfully or failed to upload
        viewModel.isUploaded.observe(this) { isUploaded ->
            unShowProgress()
            if (isUploaded) {
                Toast.makeText(this, "Details save successfully", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to save details", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun isValid(editText: EditText): Boolean {
        return if (editText.text.isBlank()) {
            editText.error = "Must not be blank"
            false
        } else {
            editText.error = null
            true
        }
    }

    private fun showProgress() {
        // show progress bar
        binding.progressBar.visibility = View.VISIBLE

        // to disable user interaction with ui
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun unShowProgress() {
        // hide progress bar
        binding.progressBar.visibility = View.GONE

        // to enable user interaction with ui
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}