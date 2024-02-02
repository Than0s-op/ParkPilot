package com.application.parkpilot.module.firebase

import com.application.parkpilot.UserCollection
import com.google.firebase.Firebase
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FireStore {

    // fireStore initialization
    private val fireStore = Firebase.firestore

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
                    it.get("first_name").toString(),
                    it.get("last_name").toString(),
                    it.get("birth_date").toString(),
                    it.get("gender").toString()
                )
            }
        }
        return result
    }

    // To get data from user collection
    suspend fun userGet(): QuerySnapshot {
        // await function this will block thread
        return fireStore.collection("user").get().await()
    }
}