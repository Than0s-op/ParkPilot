package com.application.parkpilot.module.firebase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.net.Uri
import coil.imageLoader
import coil.request.ImageRequest
import com.application.parkpilot.Utils
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class Storage {
    private val storageRef = Firebase.storage.reference

    companion object {
        private const val PROFILE = "user_profile_photo/"
        private const val SPOT = "parkSpot/"
        private const val FREE_SPOT = "free_spot/"
    }

    suspend fun userProfilePhotoPut(uid: String, uri: Uri?): Uri? {
        val childRef = storageRef.child("user_profile_photo/${uid}")
        if (uri == null) return null
        try {
            if (Utils.isLocalUri(uri)) {
                childRef.putFile(uri).await()
            }
        } catch (_: Exception) {
        }
        return userProfilePhotoGet(uid)
    }

    suspend fun userProfilePhotoGet(uid: String): Uri? {
        return try {
            storageRef.child("user_profile_photo/${uid}").downloadUrl.await()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun parkSpotPhotoGet(uid: String): List<Uri> {
        val imagesUri = ArrayList<Uri>()
        try {
            val list = storageRef.child("$SPOT${uid}/").listAll().await()
            for (i in list.items) {
                imagesUri.add(i.downloadUrl.await())
            }
        } catch (_: Exception) {
        }
        return imagesUri
    }

    suspend fun getFreeSpotImages(uid: String): List<Uri> {
        val imageList = mutableListOf<Uri>()
        try {
            val list = storageRef.child("$FREE_SPOT${uid}/").listAll().await()
            for (image in list.items) {
                imageList.add(image.downloadUrl.await())
            }
        } catch (_: Exception) {
        }
        return imageList
    }
}