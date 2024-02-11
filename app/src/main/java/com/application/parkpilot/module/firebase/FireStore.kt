package com.application.parkpilot.module.firebase

import com.application.parkpilot.QRCodeCollection
import com.application.parkpilot.UserCollection
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

open class FireStore {

    // fireStore initialization
    protected val fireStore = Firebase.firestore
}

class User : FireStore() {
    // To put date into user collection in specific document
    suspend fun userSet(data: UserCollection, documentID: String): Boolean {
        // for success result
        var result = false

        // data mapping
        val map = mapOf(
            "first_name" to data.firstName.trim(),
            "last_name" to data.lastName.trim(),
            "birth_date" to data.birthDate.trim(),
            "gender" to data.gender
        )

        // await function this will block thread
        fireStore.collection("user").document(documentID).set(map, SetOptions.merge())
            .addOnSuccessListener {
                // call successfully perform
                result = true
            }.await()

        // return result
        return result
    }

    // To get data from user collection with specific document
    suspend fun userGet(documentID: String): UserCollection? {
        var result: UserCollection? = null
        // await function this will block thread
        fireStore.collection("user").document(documentID).get().await().let {
            if (it.get("first_name") != null) {
                result = UserCollection(
                    it.get("first_name") as String,
                    it.get("last_name") as String,
                    it.get("birth_date") as String,
                    it.get("gender") as String
                )
            }
        }
        return result
    }
}

class QRCode : FireStore() {
    // To put date into user collection in specific document
    suspend fun QRCodeSet(data: QRCodeCollection, documentID: String): Boolean {
        // for success result
        var result = false

        // data mapping
        val map = mapOf(
            "QR" to mapOf(
                "key" to data.key,
                "generate" to data.generate,
                "up_to" to data.upTo,
                "valid" to data.valid
            )
        )

        // await function this will block thread
        fireStore.collection("QR_code").document(documentID).set(map, SetOptions.merge())
            .addOnSuccessListener {
                // call successfully perform
                result = true
            }.await()

        // return result
        return result
    }

    // To get data from user collection with specific document
    suspend fun QRCodeGet(documentID: String): QRCodeCollection? {
        var result: QRCodeCollection? = null
        // await function this will block thread
        fireStore.collection("QR_code").document(documentID).get().await().let { document ->
            document.get("QR")?.let{
//                result = QRCodeCollection(
//                    it.get("key") as String,
//                    it.get("up_to") as Int,
//                    it.get("generate") as Timestamp,
//                    it.get("valid") as Boolean
//                )
                println("QR: $it")
            }
        }
        return result
    }
}