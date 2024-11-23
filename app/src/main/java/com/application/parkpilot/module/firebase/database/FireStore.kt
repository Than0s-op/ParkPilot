package com.application.parkpilot.module.firebase.database

import com.application.parkpilot.AccessHours
import com.application.parkpilot.Book
import com.application.parkpilot.FreeSpotDetails
import com.application.parkpilot.QRCodeCollection
import com.application.parkpilot.StationAdvance
import com.application.parkpilot.StationBasic as StationBasic_DC
import com.application.parkpilot.StationLocation
import com.application.parkpilot.Ticket
import com.application.parkpilot.UserCollection
import com.application.parkpilot.UserProfile
import com.application.parkpilot.module.firebase.Storage
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import com.application.parkpilot.Feedback as FeedbackData

open class FireStore {
    // fireStore initialization
    protected val fireStore = Firebase.firestore
}

class UserBasic : FireStore() {
    private val collectionName = "usersBasic"
    private val userName = "userName"
    private val userPicture = "userPicture"

    // it will update user name and profile image
    suspend fun setProfile(data: UserProfile, documentID: String): Boolean {
        // to store update result
        var result: Boolean

        // data mapping
        val map = mapOf(
            userName to data.userName.trim()
        )

        try {
            fireStore
                .collection(collectionName)
                .document(documentID)
                .set(map, SetOptions.merge())
                .await()
            result = true
        } catch (e: Exception) {
            result = false
        }

        // return update status
        return result
    }

    suspend fun getProfile(documentID: String): UserProfile? {
        var result: UserProfile? = null
        // await function this will block thread
        try {
            fireStore.collection(collectionName).document(documentID).get().await().apply {
                // is firstName present? if yes, it means data are present otherwise not
                if (get(userName) != null) {
                    result = UserProfile(
                        get(userName) as String
                    )
                }
            }
        } catch (_: Exception) {

        }
        return result
    }

    suspend fun isUnique(userName: String): Boolean {
        var aggregateCount = 1L
        try {
            aggregateCount =
                fireStore.collection(collectionName).whereEqualTo(this.userName, userName).count()
                    .get(AggregateSource.SERVER).await().count
        } catch (_: Exception) {

        }
        return aggregateCount == 0L
    }
}

class UserAdvance : FireStore() {
    private val collectionName = "usersAdvance"
    private val firstName = "firstName"
    private val lastName = "lastName"
    private val birthDate = "birthDate"
    private val gender = "gender"


    // To put date into user collection in specific document
    suspend fun userSet(data: UserCollection, documentID: String): Boolean {
        // for success result
        var result: Boolean

        // data mapping
        val map = mapOf(
            firstName to data.firstName,
            lastName to data.lastName,
            birthDate to data.birthDate,
            gender to data.gender
        )

        // await function this will block thread
        try {
            fireStore
                .collection(collectionName)
                .document(documentID)
                .set(map, SetOptions.merge())
                .await()
            result = true
        } catch (_: Exception) {
            result = false
        }

        // return result
        return result
    }

    // To get data from user collection with specific document
    suspend fun userGet(documentID: String): UserCollection? {
        var result: UserCollection? = null
        // await function this will block thread
        try {
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
        } catch (_: Exception) {
            result = null
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
        var result: Boolean

        // data mapping
        val map = mapOf(
            data.key to mapOf(
                from to data.from,
                to to data.to,
                validStatus to data.valid,
            )
        )
        try {
            // await function this will block thread
            fireStore.collection(collectionName).document(documentID).set(map, SetOptions.merge())
                .await()
            result = true
        } catch (_: Exception) {
            result = false
        }

        // return result
        return result
    }
}

class StationLocation : FireStore() {
    private val collectionName = "stationsLocation"
    private val coordinates = "coordinates"
    suspend fun locationGet(): ArrayList<StationLocation> {
        // creating arraylist of station data class
        val result = ArrayList<StationLocation>()
        try {
            fireStore.collection(collectionName).get().await().let { collection ->
                for (document in collection) {
                    result.add(
                        StationLocation(
                            stationUid = document.id,
                            coordinates = document.data[coordinates] as GeoPoint,
                            isFree = false
                        )
                    )
                }
            }
        } catch (_: Exception) {

        }
        return result
    }

    suspend fun locationGet(documentID: String): GeoPoint? {
        var result: GeoPoint? = null
        try {
            // await function this will block thread
            fireStore.collection(collectionName).document(documentID).get().await().apply {
                // is coordinates present? if yes, it means data is present otherwise not
                get(coordinates)?.let {
                    result = it as GeoPoint
                }
            }
        } catch (_: Exception) {
        }
        return result
    }
}

class FreeSpot : FireStore() {
    suspend fun getLocations(): ArrayList<StationLocation> {
        val result = ArrayList<StationLocation>()
        try {
            fireStore.collection(COLLECTION_NAME).get().await().let { collection ->
                for (document in collection) {
                    result.add(
                        StationLocation(
                            stationUid = document.id,
                            coordinates = document.data[LOCATION] as GeoPoint,
                            isFree = true
                        )
                    )
                }
            }
        } catch (_: Exception) {
        }
        return result
    }

    suspend fun getDetails(documentId: String): FreeSpotDetails? {
        return try {
            val doc = fireStore.collection(COLLECTION_NAME).document(documentId).get().await()
            val storage = Storage().getFreeSpotImages(documentId)

            FreeSpotDetails(
                documentId = documentId,
                landMark = doc.data?.get(LAND_MARK) as String,
                location = doc.data?.get(LOCATION) as GeoPoint,
                policies = doc.data?.get(POLICIES) as String,
                images = storage
            )
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        const val COLLECTION_NAME = "free_spots"
        const val LOCATION = "location"
        const val LAND_MARK = "land_mark"
        const val POLICIES = "policies"
    }
}

class StationBasic : FireStore() {
    private val collectionName = "stationBasic"
    private val name = "name"
    private val price = "price"
    private val reserved = "reserved"
    suspend fun basicGet(documentID: String): StationBasic_DC? {
        var result: StationBasic_DC? = null
        fireStore.collection(collectionName).document(documentID).get().await().apply {
            if (get(name) != null) {
                result =
                    StationBasic_DC(
                        get(name) as String,
                        (get(price) as Long).toInt(),
                        (get(reserved) as Long).toInt()
                    )
            }
        }
        return result
    }
}

@Suppress("UNCHECKED_CAST")
class StationAdvance : FireStore() {
    private val collectionName = "stationAdvance"
    private val policies = "policies"
    private val amenities = "amenities"
    private val accessHours = "accessHours"
    private val gettingThere = "gettingThere"
    private val openTime = "openTime"
    private val closeTime = "closeTime"
    private val available = "available"

    suspend fun advanceGet(documentID: String): StationAdvance? {
        var result: StationAdvance? = null
        try {
            fireStore.collection(collectionName).document(documentID).get().await().apply {
                if (get(amenities) != null) {
                    result = StationAdvance(
                        get(policies) as String,
                        get(amenities) as ArrayList<String>,
                        (get(accessHours) as Map<String, Any>).let {
                            AccessHours(
                                it[openTime] as String,
                                it[closeTime] as String,
                                it[available] as List<String>
                            )
                        }
                    )
                }
            }
        } catch (_: Exception) {

        }
        return result
    }
}

class Feedback : FireStore() {
    private val collectionName = "feedback"
    private val rating = "rating"
    private val message = "message"

    @Suppress("UNCHECKED_CAST")
    suspend fun feedGet(documentID: String): Map<String, FeedbackData> {
        val result = hashMapOf<String, FeedbackData>()
        try {
            fireStore.collection(collectionName).document(documentID).get().await().let {
                val feedbacks = it.data as? Map<String, Any>

                if (feedbacks != null) {
                    for (feedback in feedbacks) {
                        feedback.apply {
                            val values = value as Map<String, Any>
                            result[key] = FeedbackData(
                                key,
                                (values[rating] as Double).toFloat(),
                                values[message] as String
                            )
                        }
                    }
                }
            }
        } catch (_: Exception) {

        }
        return result
    }

    suspend fun feedSet(feedback: FeedbackData, documentID: String): Boolean {
        // for success result
        var result = false

        // data mapping
        val map = mapOf(
            feedback.UID to mapOf(
                rating to feedback.rating,
                message to feedback.message
            )
        )
        try {
            // await function this will block thread
            fireStore.collection(collectionName).document(documentID).set(map, SetOptions.merge())
                .await()
            result = true
        } catch (_: Exception) {
        }

        // return result
        return result
    }
}

class Booking : FireStore() {
    private val collectionName = "booking"
    private val from = "from"
    private val to = "to"
    private val user = "user"
    private val station = "station"

    suspend fun bookingSet(ticket: Book): String? {
        var result = false

        val map = mapOf(
            from to ticket.fromTimestamp,
            to to ticket.toTimestamp,
            user to ticket.userID,
            station to ticket.stationID
        )
        try {
            val id = fireStore.collection(collectionName).document().id
            fireStore.collection(collectionName).document(id).set(map).addOnSuccessListener {
                result = true
            }.await()
            if (result) return id
        } catch (_: Exception) {

        }
        return null
    }

    suspend fun getCountBetween(ticket: Book): Long {
        val collection = fireStore.collection(collectionName)
        val query = collection.where(
            Filter.and(
                Filter.equalTo(station, ticket.stationID),
                Filter.or(
                    Filter.and(
                        Filter.lessThanOrEqualTo(from, ticket.toTimestamp),
                        Filter.greaterThanOrEqualTo(to, ticket.toTimestamp)
                    ),
                    Filter.and(
                        Filter.lessThanOrEqualTo(from, ticket.fromTimestamp),
                        Filter.greaterThanOrEqualTo(to, ticket.fromTimestamp)
                    ),
                    Filter.and(
                        Filter.greaterThanOrEqualTo(from, ticket.fromTimestamp),
                        Filter.lessThanOrEqualTo(to, ticket.toTimestamp)
                    )
                )
            )
        )
        return try {
            val queryResult = query.count().get(AggregateSource.SERVER).await()
            queryResult.count
        } catch (_: Exception) {
            Long.MAX_VALUE
        }
    }

    suspend fun getTicket(uid: String): ArrayList<Ticket> {
        val collection = fireStore.collection(collectionName)
        val query = collection.whereEqualTo(user, uid)
        val ticketList = ArrayList<Ticket>()
        try {
            query.get().await().let {
                for (document in it) {
                    ticketList.add(
                        Ticket(
                            document.data[from] as Timestamp,
                            document.data[to] as Timestamp,
                            StationBasic().basicGet(document.data[station] as String)?.name
                                ?: "I don't know",
                            document.id
                        )
                    )
                }
            }
        } catch (_: Exception) {
        }
        return ticketList
    }
}