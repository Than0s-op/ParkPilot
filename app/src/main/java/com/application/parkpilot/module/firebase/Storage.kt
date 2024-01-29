package com.application.parkpilot.module.firebase

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

class Storage {
    private val storageRef = Firebase.storage.reference
    suspend fun userProfilePhotoPut(userUID:String,photoUri: Uri):Uri{
        val childRef = storageRef.child("user_profile_photo/${userUID}")
        childRef.putFile(photoUri).await()
        return userProfilePhotoGet(userUID)
    }

    suspend fun userProfilePhotoGet(userUID:String):Uri{
        return storageRef.child("user_profile_photo/${userUID}").downloadUrl.await()
    }
}