package com.application.parkpilot.module.firebase

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

class Storage {
    private val storageRef = Firebase.storage.reference
    suspend fun userProfilePhotoPut(uid: String, photoUri: Uri): Uri {
        val childRef = storageRef.child("user_profile_photo/${uid}")
        childRef.putFile(photoUri).await()
        return userProfilePhotoGet(uid)
    }

    suspend fun userProfilePhotoGet(uid: String): Uri {
        return storageRef.child("user_profile_photo/${uid}").downloadUrl.await()
    }

    suspend fun parkSpotPhotoPut(uid: String, photosUri: Array<Uri?>):Boolean {
        val path = "parkSpot/${uid}/"
        var result = true

        for ((cnt, uri) in photosUri.withIndex()) {
            val childRef = storageRef.child("$path${cnt}")
            if(uri == null){
                childRef.delete()
                continue
            }
            childRef.putFile(uri).addOnFailureListener{
                result = false
            }.await()
        }
        return result
    }
    suspend fun parkSpotPhotoGet(uid:String):List<Uri>{
        val list = storageRef.child("parkSpot/${uid}/").listAll().await()
        val imagesUri = ArrayList<Uri>()
        for(i in list.items){
            imagesUri.add(i.downloadUrl.await())
        }
        return imagesUri
    }
}