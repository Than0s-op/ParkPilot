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
    private val collectionName = "users"
    private val firstName = "firstName"
    private val lastName = "lastName"
    private val birthDate = "birthDate"
    private val gender = "gender"


    // To put date into user collection in specific document
    suspend fun userSet(data: UserCollection, documentID: String): Boolean {
        // for success result
        var result = false

        // data mapping
        val map = mapOf(
            firstName to data.firstName.trim(),
            lastName to data.lastName.trim(),
            birthDate to data.birthDate.trim(),
            gender to data.gender
        )

        // await function this will block thread
        fireStore.collection(collectionName).document(documentID).set(map, SetOptions.merge())
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
        fireStore.collection(collectionName).document(documentID).get().await().apply {
            // is firstName present? if yes, it means data are present otherwise not
            if (get(firstName) != null) {
                result = UserCollection(
                    get(firstName) as String,
                    get(lastName) as String,
                    get(birthDate) as String,
                    get(gender) as String
                )
            }
        }
        return result
    }
}

class QRCode : FireStore() {
    private val collectionName = "qrCodes"
    private val from = "from"
    private val to = "to"
    private val validStatus = "valid"

    // To put date into user collection in specific document
    suspend fun QRCodeSet(data: QRCodeCollection, documentID: String): Boolean {
        // for success result
        var result = false

        // data mapping
        val map = mapOf(
            data.key to mapOf(
                from to data.from,
                to to data.to,
                validStatus to data.valid,
            )
        )

        // await function this will block thread
        fireStore.collection(collectionName).document(documentID).set(map, SetOptions.merge())
            .addOnSuccessListener {
                // call successfully perform
                result = true
            }.await()

        // return result
        return result
    }

    // To get data from user collection with specific document
    @Suppress("UNCHECKED_CAST")
    suspend fun QRCodeGet(documentID: String): ArrayList<QRCodeCollection> {
        val result: ArrayList<QRCodeCollection> = ArrayList()
        // await function this will block thread
        fireStore.collection(collectionName).document(documentID).get().await().let { document ->
            document.data?.let {
                val fields = it as Map<String, Map<String, Any>>
                for (field in fields) {
                    result.add(
                        QRCodeCollection(
                            key = field.key,
                            from = field.value[from] as Timestamp,
                            to = (field.value[to] as Long).toInt(),
                            valid = field.value[validStatus] as Boolean,
                        )
                    )
                }
            }
        }
        return result
    }
}

class StationLocation : FireStore() {
    private val collectionName = "stationsLocation"
    private val coordinates = "coordinates"
    suspend fun locationGet(): ArrayList<StationLocation> {
        // creating arraylist of station data class
        val result = ArrayList<StationLocation>()

        fireStore.collection(collectionName).get().await().let { collection ->
            for (document in collection) {
                result.add(StationLocation(document.id, document.data[coordinates] as GeoPoint))
            }
        }
        return result
    }
}

class StationBasic : FireStore() {
    private val collectionName = "stationBasic"
    private val name = "name"
    private val price = "price"
    private val rating = "rating"
    suspend fun basicGet(documentID: String): StationBasic? {
        var result: StationBasic?
        fireStore.collection(collectionName).document(documentID).get().await().apply {
            result =
                StationBasic(
                    get(name) as String,
                    (get(price) as Long).toInt(),
                    (get(rating) as Double).toFloat()
                )
        }
        return result
    }
}

@Suppress("UNCHECKED_CAST")
class StationAdvance : FireStore() {
    private val collectionName = "stationAdvance"
    private val thinkShouldYouKnow = "ThinkShouldYouKnow"
    private val amenities = "amenities"
    private val accessHours = "accessHours"
    private val gettingThere = "gettingThere"

    suspend fun advanceGet(documentID: String): StationAdvance? {
        val result: StationAdvance?
        fireStore.collection(collectionName).document(documentID).get().await().apply {
            result = StationAdvance(
                get(thinkShouldYouKnow) as ArrayList<String>,
                get(amenities) as ArrayList<String>,
                get(accessHours) as String,
                get(gettingThere) as String
            )
        }
        return result
    }
}