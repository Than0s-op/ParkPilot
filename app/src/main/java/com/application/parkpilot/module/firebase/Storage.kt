package com.application.parkpilot.module.firebase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.net.Uri
import coil.imageLoader
import coil.request.ImageRequest
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class Storage {
    private val storageRef = Firebase.storage.reference

    companion object {
        private const val PROFILE = "user_profile_photo/"
        private const val SPOT = "parkSpot/"
    }

    suspend fun userProfilePhotoPut(context: Context, uid: String, image: Any): Uri? {
        val childRef = storageRef.child("$PROFILE${uid}")
        val data = compress(context, image)
        childRef.putBytes(data).await()
        return userProfilePhotoGet(uid)
    }

    suspend fun userProfilePhotoGet(uid: String): Uri? {
        return try {
            storageRef.child("$PROFILE${uid}").downloadUrl.await()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun parkSpotPhotoGet(uid: String): List<Uri> {
        val list = storageRef.child("$SPOT${uid}/").listAll().await()
        val imagesUri = ArrayList<Uri>()
        for (i in list.items) {
            imagesUri.add(i.downloadUrl.await())
        }
        return imagesUri
    }

    private suspend fun compress(context: Context, image: Any): ByteArray {
        val request = ImageRequest.Builder(context)
            .data(image)
            .size(600, 600)
            .build()
        val drawable = context.imageLoader.execute(request).drawable

        // this next level logic present on firebase doc
        val bitmap = (drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }
}