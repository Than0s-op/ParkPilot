package com.application.parkpilot.viewModel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.size.Scale
import com.application.parkpilot.R
import com.application.parkpilot.UserCollection
import com.application.parkpilot.UserProfile
import com.application.parkpilot.activity.MainActivity
import com.application.parkpilot.activity.UserRegisterActivity
import com.application.parkpilot.module.DatePicker
import com.application.parkpilot.module.PhotoPicker
import com.application.parkpilot.module.firebase.FireStore
import com.application.parkpilot.module.firebase.Storage
import com.application.parkpilot.module.firebase.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class UserRegisterViewModel(activity: UserRegisterActivity) : ViewModel() {
    // getting current user status
    val user: FirebaseUser = Firebase.auth.currentUser!!

    // it will store user profile image's Uri
    var photoUrl: Uri? = user.photoUrl

    // live data, it will help to "View" to get data
    val userInformation = MutableLiveData<UserCollection?>()
    val imageLoaderResult = MutableLiveData<ImageResult>()
    val isUploaded = MutableLiveData<Boolean>()

    // lazy object creation
    private val fireStore by lazy {User()}
    private val storage by lazy {Storage()}
    val datePicker by lazy{DatePicker(activity)}
    val photoPicker by lazy{PhotoPicker(activity)}


    // it will store MainActivity intent or null
    // why it's here? ans:- [ if user came from Main Activity then we have to throw user again to Main Activity, otherwise do nothing]
    var nextIntent: Intent? = Intent(activity, MainActivity::class.java)

    // it will get user detail from user collection
    fun getUserDetails() {
        viewModelScope.launch {
            // call to fireStore
            userInformation.value = fireStore.userGet(user.uid)
        }
    }

    // it will upload user detail in user's collection
    private suspend fun setUserDetails(details: UserCollection):Boolean {
        return fireStore.userSet(details, user.uid)
    }

    // it will update user name and profile image
    private suspend fun updateProfile(data: UserProfile):Boolean{
        // to store update result
        var isUpdateSuccessfully = false

        // if (photoUrl == user.photoUrl) it means user didn't choice any image
        // other wise
        val storageProfilePhotoUri =
            if (photoUrl != user.photoUrl) storage.userProfilePhotoPut(user.uid, photoUrl!!)
            else user.photoUrl!!

        // creating profile update request
        val profileUpdates = userProfileChangeRequest {
            displayName = data.displayName.trim()
            photoUri = storageProfilePhotoUri
        }

        // To update profile
        user.updateProfile(profileUpdates).addOnSuccessListener {
            isUpdateSuccessfully = true
        }.await()

        // return update status
        return isUpdateSuccessfully
    }

    //
    fun saveUserData(userCollection:UserCollection,userProfile:UserProfile){
        viewModelScope.launch{
            isUploaded.value = setUserDetails(userCollection) and updateProfile(userProfile)
        }
    }

    // format should be in day,month,year
    fun getAge(birthDate: String): String {
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

    fun getImage(
        context: Context, imageUrl: Any, width: Int = 192, height: Int = 192
    ) {
        // request for profile image of user
        val profileImageRequest = ImageRequest.Builder(context)
            .data(imageUrl)
            .size(width, height)
            .scale(Scale.FIT)
            .build()

        viewModelScope.launch {
            imageLoaderResult.value = context.imageLoader.execute(profileImageRequest)
        }
    }
}