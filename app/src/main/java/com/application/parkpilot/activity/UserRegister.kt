package com.application.parkpilot.activity

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.application.parkpilot.R
import com.application.parkpilot.UserCollection
import com.application.parkpilot.UserProfile
import com.application.parkpilot.Utils
import com.application.parkpilot.databinding.UserRegisterBinding
import com.application.parkpilot.viewModel.UserRegisterViewModel

class UserRegister : AppCompatActivity() {
    private lateinit var binding: UserRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = UserRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // getting userRegister view model reference
        val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UserRegisterViewModel(this@UserRegister) as T
            }
        })[UserRegisterViewModel::class.java]

        viewModel.getProfileDetails({
            binding.shimmerLayout.shimmerLayout.visibility = View.GONE
            binding.linearLayout.visibility = View.VISIBLE
        })

        // get user details from user collection
//        viewModel.getUserDetails()

        binding.editTextBirthDate.setOnClickListener {
            // start and end dates format should be yyyy-mm-dd (modify this function)
            viewModel.datePicker.show(this, "Pick birth date")
        }

        binding.imageViewProfilePicture.setOnClickListener {
            // start photo picker
            viewModel.photoPicker.showPhotoPicker()
        }

        binding.editTextUserName.addTextChangedListener {
            it.toString().apply {
                if (viewModel.userProfile.value?.userName != this) {
                    viewModel.isUnique(this)
                } else {
                    viewModel.isUnique.value = true
                }
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.buttonSave.setOnClickListener {
            var isValid = true

            if (isInvalidName(binding.editTextFirstName.text.toString())) {
                binding.editTextFirstName.error = "Field must contain [A-Z] [a-z] characters"
                isValid = false
            }
            if (isInvalidName(binding.editTextLastName.text.toString())) {
                binding.editTextLastName.error = "Field must contain [A-Z] [a-z] characters"
                isValid = false
            }
            if (isInvalidUserName(binding.editTextUserName.text.toString())) {
                binding.editTextUserName.error =
                    "Field must contain [A-Z] [a-z] [0-9] [@_$] characters"
                isValid = false
            }
            if (binding.editTextAge.text.toString() < "18") {
                binding.editTextAge.error = "Age must be 18 or above"
                isValid = false
            }

            if (isValid && viewModel.isUnique.value == true) {
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

        viewModel.isUnique.observe(this) { isUnique ->
            if (!isUnique) {
                binding.editTextUserName.error = "Already taken"
            }
        }

        // it will execute when user data uploaded successfully or failed to upload
        viewModel.isUploaded.observe(this) { isUploaded ->
            unShowProgress()
            if (isUploaded) {
                Utils.truthToast(this, "Details save successfully")
                finish()
            } else {
                Utils.errorToast(this, "Failed to save details")
            }
        }
    }

    private fun isInvalidName(name: String): Boolean {
        val pattern = Regex("[A-Za-z]+")
        return !pattern.matches(name)
    }

    private fun isInvalidUserName(name: String): Boolean {
        val pattern = Regex("[A-Za-z0-9@_$]+")
        return !pattern.matches(name)
    }

    private fun showProgress() {
        // show progress bar
        binding.progressBar.visibility = View.VISIBLE
        binding.buttonSave.visibility = View.GONE

        // to disable user interaction with ui
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun unShowProgress() {
        // hide progress bar
        binding.progressBar.visibility = View.GONE
        binding.buttonSave.visibility = View.VISIBLE

        // to enable user interaction with ui
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}