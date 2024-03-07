package com.application.parkpilot.module.firebase

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

class Storage {
    private val storageRef = Firebase.storage.reference
    suspend fun userProfilePhotoPut(userUID: String, photoUri: Uri): Uri? {
        var result: Uri? = null
        val childRef = storageRef.child("user_profile_photo/${userUID}")
        childRef.putFile(photoUri).continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            childRef.downloadUrl
        }.addOnCompleteListener { task ->
            result = task.result
        }.await()
        return result
    }
}