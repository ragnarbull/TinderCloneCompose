package com.apiguave.tinderclonecompose.data.datasource

import android.annotation.SuppressLint
import android.app.Application
import android.icu.util.Calendar
import android.util.Log
import android.widget.Toast
import com.apiguave.tinderclonecompose.data.datasource.model.*
import com.apiguave.tinderclonecompose.data.datasource.exception.AuthException
import com.apiguave.tinderclonecompose.data.datasource.exception.FirestoreException
import com.apiguave.tinderclonecompose.domain.message.entity.Message
import com.apiguave.tinderclonecompose.domain.profile.entity.Orientation
import com.apiguave.tinderclonecompose.data.datasource.model.FirestoreUserList
import com.apiguave.tinderclonecompose.domain.discoverysettingscard.entity.CurrentDiscoverySettings
import com.apiguave.tinderclonecompose.domain.profile.entity.UserLocation
import com.apiguave.tinderclonecompose.extensions.getTaskResult
import com.apiguave.tinderclonecompose.extensions.toTimestamp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.util.Date
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class FirestoreRemoteDataSource(
    private val appContext: Application
) {
    companion object {
        private val TAG = "FSRemoteDataSource"
        private const val USERS = "users"
        private const val MATCHES = "matches"
        private const val MESSAGES = "messages"
    }

    private val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: throw AuthException("User not logged in")

    suspend fun updateProfileData(data: Map<String, Any>){
        FirebaseFirestore.getInstance().collection(USERS).document(currentUserId).update(data).getTaskResult()
    }

    fun getMessages(matchId: String): Flow<List<Message>> = callbackFlow {
        var eventsCollection: CollectionReference? = null
        try {
            eventsCollection = FirebaseFirestore.getInstance()
                .collection(MATCHES)
                .document(matchId).collection(MESSAGES)
        } catch (e: Throwable) {
            // If Firebase cannot be initialized, close the stream of data
            // flow consumers will stop collecting and the coroutine will resume
            close(e)
        }

        // Registers callback to firestore, which will be called on new events
        val subscription = eventsCollection?.orderBy(FirestoreMessageProperties.timestampKey)?.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) { return@addSnapshotListener }
            // Sends events to the flow! Consumers will get the new events
            try {
                val messages = snapshot.toObjects(FirestoreMessage::class.java).map {
                    val id = it.id
                    val isSender = it.senderId == currentUserId
                    val liked = it.liked
                    val text = it.message
                    val giphyMediaId = it.giphyMediaId
                    if (text != null) {
                        // text message
                        Message(id, isSender, liked, text, null)
                    } else {
                        // gif
                        Message(id, isSender, liked, null, giphyMediaId)
                    }
                }
                trySend(messages)
            }catch (e: Exception){
                close(e)
            }
        }

        // The callback inside awaitClose will be executed when the flow is
        // either closed or cancelled.
        // In this case, remove the callback from Firestore
        awaitClose { subscription?.remove() }
    }

    suspend fun sendMessage(matchId: String, text: String){
        val newMessageRef = FirebaseFirestore.getInstance()
            .collection(MATCHES)
            .document(matchId)
            .collection(MESSAGES)
            .document() // Automatically generates a unique ID for the message document

        val data = FirestoreMessageProperties.toData(newMessageRef.id, currentUserId, false, text,null)

        coroutineScope {
            val newMessageResult = async {
                newMessageRef.set(data).getTaskResult()
            }
            val lastMessageResult = async {
                FirebaseFirestore.getInstance()
                    .collection(MATCHES)
                    .document(matchId)
                    .update(mapOf(FirestoreMatchProperties.lastMessage to text))
                    .getTaskResult()
            }
            newMessageResult.await()
            lastMessageResult.await()
        }
    }

    suspend fun sendGiphyGif(matchId: String, giphyMediaId: String){
        val newMessageRef = FirebaseFirestore.getInstance()
            .collection(MATCHES)
            .document(matchId)
            .collection(MESSAGES)
            .document() // Automatically generates a unique ID for the message document

        val data = FirestoreMessageProperties.toData(newMessageRef.id, currentUserId, false, null, giphyMediaId)

        coroutineScope {
            val newMessageResult = async {
                newMessageRef.set(data).getTaskResult()
            }
            val lastMessageResult = async {
                FirebaseFirestore.getInstance()
                    .collection(MATCHES)
                    .document(matchId)
                    .update(mapOf(FirestoreMatchProperties.lastMessage to "You sent a GIF"))// TODO: display the other user's name if they sent it
                    .getTaskResult()
            }
            newMessageResult.await()
            lastMessageResult.await()
        }
    }

    @SuppressLint("LogNotTimber")
    suspend fun likeMessage(matchId: String, messageId: String) {
        try {
            val messageRef = FirebaseFirestore.getInstance()
                .collection(MATCHES)
                .document(matchId)
                .collection(MESSAGES)
                .document(messageId)

            messageRef.update("liked", true).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error liking message: $e")
            // Handle error appropriately
        }
    }

    @SuppressLint("LogNotTimber")
    suspend fun unLikeMessage(matchId: String, messageId: String) {
        try {
            val messageRef = FirebaseFirestore.getInstance()
                .collection(MATCHES)
                .document(matchId)
                .collection(MESSAGES)
                .document(messageId)

            messageRef.update("liked", false).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error unliking message: $e")
            // Handle error appropriately
        }
    }

    @SuppressLint("LogNotTimber")
    suspend fun swipeUser(swipedUserId: String, isLike: Boolean): FirestoreMatch? {
        FirebaseFirestore.getInstance()
            .collection(USERS)
            .document(currentUserId)
            .update(mapOf((if (isLike) FirestoreUserProperties.liked else FirestoreUserProperties.passed) to FieldValue.arrayUnion(swipedUserId)))
            .getTaskResult()
        FirebaseFirestore.getInstance()
            .collection(USERS)
            .document(currentUserId)
            .collection(FirestoreUserProperties.liked)
            .document(swipedUserId)
            .set(mapOf("exists" to true))
            .getTaskResult()

        val hasUserLikedBack = hasUserLikedBack(swipedUserId)
        if (hasUserLikedBack) {
            val matchId = getMatchId(currentUserId, swipedUserId)
            Log.d(TAG, "matchId $matchId")
            FieldValue.serverTimestamp()
            val data = FirestoreMatchProperties.toData(swipedUserId, currentUserId)
            FirebaseFirestore.getInstance()
                .collection(MATCHES)
                .document(matchId)
                .set(data)
                .getTaskResult()

            val result = FirebaseFirestore.getInstance()
                .collection(MATCHES)
                .document(matchId)
                .get()
                .getTaskResult()
            return result.toObject(FirestoreMatch::class.java)
        } else {
            return null
        }
    }

    private fun getMatchId(userId1: String, userId2: String): String{
        return if(userId1 > userId2){
            userId1 + userId2
        } else userId2 + userId1
    }

    private suspend fun hasUserLikedBack(swipedUserId: String): Boolean{
        val result = FirebaseFirestore.getInstance()
            .collection(USERS)
            .document(swipedUserId)
            .collection(FirestoreUserProperties.liked)
            .document(currentUserId)
            .get()
            .getTaskResult()
        return result.exists()
    }

    suspend fun createUserProfile(
        userId: String,
        name: String,
        birthdate: LocalDate,
        bio: String,
        isMale: Boolean,
        location: UserLocation,
        orientation: Orientation,
        maxDistance: Int,
        minAge: Int,
        maxAge: Int,
        height: String,
        languages: String,
        jobTitle: String,
        zodiacSign: String,
        education: String,
        interests: String,
        pictures: List<String>
        ) {
        val user = FirestoreUser(
            name = name,
            birthDate = birthdate.toTimestamp(),
            bio = bio,
            male = isMale,
            location = location,
            orientation = orientation,
            maxDistance = maxDistance,
            minAge = minAge,
            maxAge = maxAge,
            height = height,
            languages = languages,
            jobTitle = jobTitle,
            zodiacSign = zodiacSign,
            education = education,
            interests = interests,
            liked = emptyList(),
            passed = emptyList(),
            pictures = pictures,
            )
        FirebaseFirestore.getInstance().collection(USERS).document(userId).set(user).getTaskResult()
    }

    @SuppressLint("LogNotTimber")
    suspend fun getUserList(): FirestoreUserList {
        // Get current user information
        val currentUser = getFirestoreUserModel(currentUserId)
        currentUser.male ?: throw FirestoreException("Couldn't find field 'isMale' for the current user.")
        val currentUserLocation = currentUser.location ?: throw FirestoreException("Couldn't find field 'location' for the current user.")
        Log.d(TAG, "Current user: ${currentUser.name}, location: $currentUserLocation") // Log the current user's location
        val currentUserMaxDistance: Int = currentUser.maxDistance
        Log.d(TAG, "Current user: ${currentUser.name}, max distance: $currentUserMaxDistance km") // Log the current user's max distance
        val currentUserBirthDate: Date = currentUser.birthDate?.toDate() ?: throw IllegalArgumentException("User's birth date is null")
//        Log.d(TAG, "Current user: ${currentUser.name}, birthdate: $currentUserBirthDate") // Log the current user's birthdate

        // Exclude the current user and user's already swiped on from the results
        val excludedUserIds = currentUser.liked + currentUser.passed + currentUserId

        // Calculate current user's target age range
        val currentUserMinBirthDate = Calendar.getInstance().apply {
            add(Calendar.YEAR, -currentUser.maxAge)
        }.time
        val currentUserMaxBirthDate = Calendar.getInstance().apply {
            add(Calendar.YEAR, -currentUser.minAge)
        }.time
        Log.d(TAG, "Current user: ${currentUser.name}, min birthdate: $currentUserMinBirthDate, max birthdate: $currentUserMaxBirthDate") // Log the current user's calculated age range

        // Query to filter documents based on birth date range
        val birthDateQuery = FirebaseFirestore.getInstance().collection(USERS)
            .whereLessThanOrEqualTo(FirestoreUserProperties.birthDate, currentUserMaxBirthDate)
            .whereGreaterThanOrEqualTo(FirestoreUserProperties.birthDate, currentUserMinBirthDate)

        val birthDateResult = birthDateQuery.get().getTaskResult()

        // Filter documents based on orientation locally
        val outsideCurrentUserRadiusUsers: MutableList<FirestoreUser> = mutableListOf()
        val ageIncompatibleUsers: MutableList<FirestoreUser> = mutableListOf()
        val orientationIncompatibleUsers: MutableList<FirestoreUser> = mutableListOf()
        val distanceIncompatibleUsers: MutableList<FirestoreUser> = mutableListOf()
        val compatibleUsers: MutableList<FirestoreUser> = mutableListOf()

        for (userSnapshot in birthDateResult) {
            val user = userSnapshot.toObject<FirestoreUser>()
            if (excludedUserIds.contains(userSnapshot.id)) continue
            user.male ?: throw FirestoreException("Couldn't find field 'isMale' for the user.")
            val userMaxDistance = user.maxDistance
            Log.d(TAG, "User: ${user.name}, Max distance: $userMaxDistance km") // Log the user's max distance
            val userLocation = user.location ?: throw FirestoreException("Couldn't find field 'location' for the user.")
            Log.d(TAG, "User: ${user.name}, location: $userLocation") // Log the user's location
            val userBirthDate: Date = user.birthDate?.toDate() ?: throw IllegalArgumentException("User's birth date is null")
//            Log.d(TAG, "User: ${user.name}, birthdate: $userBirthDate") // Log the user's birthdate

            // Check if the user's age range includes the current user's age range
            val userMinBirthDate = Calendar.getInstance().apply {
                add(Calendar.YEAR, -currentUser.maxAge)
            }.time
            val userMaxBirthDate = Calendar.getInstance().apply {
                add(Calendar.YEAR, -currentUser.minAge)
            }.time
//            Log.d(TAG, "User: ${user.name}, min birthdate: $userMinBirthDate, max birthdate: $userMaxBirthDate") // Log the user's calculated age range

            if (currentUserMaxBirthDate >= userMinBirthDate && currentUserMinBirthDate <= userMaxBirthDate) {
                // Check orientation and exclude user if necessary
                if (currentUser.orientation == Orientation.both ||
                    (currentUser.orientation == Orientation.men && user.orientation == Orientation.women) ||
                    (currentUser.orientation == Orientation.women && user.orientation == Orientation.men)
                ) {
                    // Calculate distance between current user and the user
                    val distance = calculateDistance(
                        currentUserLocation.latitude, currentUserLocation.longitude,
                        userLocation.latitude, userLocation.longitude
                    )
                    Log.d(TAG, "User: ${user.name}, distance: $distance km") // Log the user's distance from the current user

                    if (distance <= currentUserMaxDistance) {
                        // Check if the user's maxDistance covers the current user's location
                        if (distance <= userMaxDistance) compatibleUsers.add(user)
                        else distanceIncompatibleUsers.add(user)
                    } else outsideCurrentUserRadiusUsers.add(user)
                } else orientationIncompatibleUsers.add(user)
            } else ageIncompatibleUsers.add(user)
        }

        Log.d(TAG, "Incompatible user profiles (outside current user radius): $outsideCurrentUserRadiusUsers")
        Log.d(TAG, "Incompatible user profiles (age): $ageIncompatibleUsers")
        Log.d(TAG, "Incompatible user profiles (orientation): $orientationIncompatibleUsers")
        Log.d(TAG, "Incompatible user profiles (distance): $distanceIncompatibleUsers")
        Log.d(TAG, "Compatible user profiles: $compatibleUsers")

        return FirestoreUserList(currentUser, compatibleUsers)
    }

    suspend fun getSavedDiscoverySettings(): CurrentDiscoverySettings {
        val currentUser = getFirestoreUserModel(currentUserId)
        val maxDistance = currentUser.maxDistance
        val minAge = currentUser.minAge
        val maxAge = currentUser.maxAge
        return CurrentDiscoverySettings(maxDistance, minAge, maxAge)
    }

    suspend fun getSavedLocation(): UserLocation {
        val currentUser = getFirestoreUserModel(currentUserId)
        return currentUser.location ?: UserLocation(0.0000, 0.0000)
    }

    suspend fun getFirestoreMatchModels(): List<FirestoreMatch> {
        val query = FirebaseFirestore.getInstance().collection(MATCHES)
            .whereArrayContains(FirestoreMatchProperties.usersMatched, currentUserId)
        val result = query.get().getTaskResult()
        return result.toObjects(FirestoreMatch::class.java)
    }

    @SuppressLint("LogNotTimber")
    suspend fun getFirestoreUserModel(userId: String): FirestoreUser {
        try {
            val snapshot = FirebaseFirestore.getInstance().collection(USERS).document(userId).get().getTaskResult()
            return snapshot.toObject<FirestoreUser>() ?: throw FirestoreException("Document doesn't exist")
        } catch (e: FirebaseFirestoreException) {
            if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                // Show a toast if the error is due to AppCheck permission denial
                Toast.makeText(appContext, "Firestore AppCheck Failed", Toast.LENGTH_SHORT).show()
            } else {
                Log.e(TAG, "Firestore Exception: ", e)
            }
            throw FirestoreException("FirebaseFirestoreException occurred")
        }
    }

    // Function to calculate distance between two points using Haversine formula
    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return 6371 * c // Radius of Earth in kilometers (note this changes based on position)
    }

    suspend fun getFirestoreMatchById(matchId: String): FirestoreMatch? {
        val matchDocument =
            FirebaseFirestore.getInstance().collection(MATCHES).document(matchId).get()
                .getTaskResult()
        return matchDocument.toObject<FirestoreMatch>()
    }

    @SuppressLint("LogNotTimber")
    suspend fun undoSwipe(swipeDirection: Int): Boolean {
        val swipedRight = swipeDirection == 2 || swipeDirection == 3 // TODO: add logic for super like
        val fieldToUpdate = if (swipedRight) FirestoreUserProperties.liked else FirestoreUserProperties.passed
        val userListRef = FirebaseFirestore.getInstance().collection(USERS).document(currentUserId)

        try {
            // Fetch the current user's document from Firestore
            val currentUserDoc = userListRef.get().await()
            val currentUserData = currentUserDoc.toObject(FirestoreUser::class.java)

            currentUserData?.let { user ->
                val userList = if (swipedRight) {
                    user.liked.toMutableList()
                } else {
                    user.passed.toMutableList()
                }

                if (userList.isNotEmpty()) {
                    // Remove the last swiped user from the liked or passed list
                    val swipedUserId = userList.removeLast()

                    // Update the Firestore document with the modified liked or passed list
                    userListRef.update(fieldToUpdate, userList).await()

                    // Remove the swiped user ID from the liked or passed collection
                    val swipedUserRef = userListRef.collection(fieldToUpdate).document(swipedUserId)
                    swipedUserRef.delete().await()

                    return true // Undo swipe successful
                } else {
                    Log.d(TAG, "User list is empty")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error undoing swipe: ${e.message}")
        }
        return false // Undo swipe unsuccessful
    }

    suspend fun fetchCurrentUser(): FirestoreUser {
        return getFirestoreUserModel(currentUserId)
    }

    private fun calculateAge(birthDate: Timestamp?): Int {
        if (birthDate == null) return 0

        val birthCalendar = Calendar.getInstance().apply {
            timeInMillis = birthDate.seconds * 1000
        }
        val currentCalendar = Calendar.getInstance()
        var age = currentCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
        if (currentCalendar.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }
}