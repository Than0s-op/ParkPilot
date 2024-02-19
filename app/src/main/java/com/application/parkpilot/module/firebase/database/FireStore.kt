package com.application.parkpilot.module.firebase.database

import com.application.parkpilot.QRCodeCollection
import com.application.parkpilot.StationAdvance
import com.application.parkpilot.StationBasic
import com.application.parkpilot.StationLocation
import com.application.parkpilot.UserCollection
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
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
            data.key to mapOf(
                "generate" to data.generate,
                "up_to" to data.upTo,
                "valid" to data.valid,
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
    suspend fun QRCodeGet(documentID: String): ArrayList<QRCodeCollection> {
        val result: ArrayList<QRCodeCollection> = ArrayList()
        // await function this will block thread
        fireStore.collection("QR_code").document(documentID).get().await().let { document ->
            document.data?.let {
                val fields = it as Map<String, Map<String, Any>>
                for (field in fields) {
                    result.add(
                        QRCodeCollection(
                            field.key,
                            (field.value["up_to"] as Long).toInt(),
                            field.value["generate"] as Timestamp,
                            field.value["valid"] as Boolean,
                        )
                    )
                }
            }
        }
        return result
    }
}

class Station : FireStore() {
    suspend fun locationGet(): ArrayList<StationLocation> {
        // creating arraylist of station data class
        val result = ArrayList<StationLocation>()

        fireStore.collection("station").get().await().let { collection ->
            for (document in collection) {
                result.add(StationLocation(document.id, document.data["coordinates"] as GeoPoint))
            }
        }
        return result
    }

    suspend fun basicGet(documentID: String): StationBasic? {
        var result: StationBasic?
        fireStore.collection("station_basic").document(documentID).get().await().apply {
            result =
                StationBasic(get("name") as String, get("price") as String, get("rating") as Float)
        }
        return result
    }

    suspend fun advanceGet(documentID: String): StationAdvance? {
        val result: StationAdvance?
        fireStore.collection("station_advance").document(documentID).get().await().apply {
            // TSYK = think should you know
            result = StationAdvance(
                get("TSYK") as Array<String>,
                get("amenities") as Map<String, Boolean>,
                get("accessHours") as String,
                get("gettingThere") as String
            )
        }
        return result
    }
}