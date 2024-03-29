package com.application.parkpilot.viewModel

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.size.Scale
import com.application.parkpilot.User
import com.application.parkpilot.UserCollection
import com.application.parkpilot.UserProfile
import com.application.parkpilot.activity.MainActivity
import com.application.parkpilot.activity.UserRegisterActivity
import com.application.parkpilot.module.DatePicker
import com.application.parkpilot.module.PhotoPicker
import com.application.parkpilot.module.firebase.Storage
import com.application.parkpilot.module.firebase.database.UserAdvance
import com.application.parkpilot.module.firebase.database.UserBasic
import com.avatarfirst.avatargenlib.AvatarGenerator
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class UserRegisterViewModel(activity: UserRegisterActivity) : ViewModel() {
    // it will store user profile image's Uri
    var photoUrl: Uri? = null

    // live data, it will help to "View" to get data
    val userInformation = MutableLiveData<UserCollection?>()
    val userProfile = MutableLiveData<UserProfile?>()
    val imageLoaderResult = MutableLiveData<ImageResult>()
    val isUploaded = MutableLiveData<Boolean>()

    // lazy object creation
    private val userBasic by lazy { UserBasic() }
    private val userAdvance by lazy { UserAdvance() }
    private val storage by lazy { Storage() }
    val datePicker by lazy { DatePicker(activity) }
    val photoPicker by lazy { PhotoPicker(activity) }


    // it will store MainActivity intent or null
    // why it's here? ans:- [ if user came from Main Activity then we have to throw user again to Main Activity, otherwise do nothing]
    var nextIntent: Intent? = Intent(activity, MainActivity::class.java)


    // it will get user detail from user collection
    fun getUserDetails() {
        viewModelScope.launch {
            // call to fireStore
            userInformation.value = userAdvance.userGet(User.UID)
        }
    }

    fun getProfileDetails() {
        viewModelScope.launch {
            val result = userBasic.getProfile(User.UID)
            if (result != null) {
                userProfile.value =
                    UserProfile(result.userName, storage.userProfilePhotoGet(User.UID))
            } else {
                if (Firebase.auth.currentUser?.displayName != null) {
                    userProfile.value = UserProfile(
                        Firebase.auth.currentUser!!.displayName!!,
                        Firebase.auth.currentUser!!.photoUrl
                    )
                } else userProfile.value = null
            }
        }
    }

    //
    fun saveUserData(context: Context, userCollection: UserCollection, userProfile: UserProfile) {
        var result = true
        viewModelScope.launch {
            result = result and userAdvance.userSet(userCollection, User.UID)

            storage.userProfilePhotoPut(
                context,
                User.UID,
                photoUrl ?: getAvatar(context, userProfile.userName)
            )

            result =
                result and userBasic.setProfile(UserProfile(userProfile.userName), User.UID)

            isUploaded.value = result
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

    fun getAvatar(context: Context, userName: String): Bitmap {
        return AvatarGenerator.AvatarBuilder(context)
            .setLabel(userName)
            .setAvatarSize(120)
            .setTextSize(30)
            .toCircle()
            .build().bitmap
    }
}