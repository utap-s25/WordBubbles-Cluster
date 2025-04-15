package com.example.wordbubblescluster

import android.app.Activity
import com.example.wordbubblescluster.model.Profile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ViewModelDBHelper {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "users"

    // If we want to listen for real time updates use this
    // .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
    fun addLeaderboardSnapshotListener(activity: Activity, callback: (List<Profile>) -> Unit) {
        db.collection(rootCollection).addSnapshotListener(activity) { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                callback(
                    snapshot.documents.mapNotNull {
                        it.toObject(Profile::class.java)
                    }
                )
            }
        }
    }



    fun getProfileByUid(uid: String, resultListener: (Profile) -> Unit) {
        db.collection(rootCollection).document(uid).get()
            .addOnSuccessListener { result ->
                result.toObject(Profile::class.java)?.let {
                    resultListener(it)
                } ?: run {
                    resultListener(Profile())
                }
            }
            .addOnFailureListener {
                resultListener(Profile())
            }
    }

    fun getLeaderboard(resultListener: (List<Profile>) -> Unit) {
        val direction = Query.Direction.DESCENDING
        val orderBy = "levelsCompleted"
        val query = db.collection(rootCollection)
            .orderBy(orderBy, direction)
        query.get()
            .addOnSuccessListener { result ->
                resultListener(result.documents.mapNotNull {
                    it.toObject(Profile::class.java)
                })
            }
            .addOnFailureListener {
                resultListener(listOf())
            }
    }

    fun createProfile(profile: Profile, resultListener: (Profile) -> Unit) {
        db.collection(rootCollection).document(profile.uid).set(profile)
            .addOnSuccessListener {
                getProfileByUid(profile.uid, resultListener)
            }
    }

    fun updateProfileName(uid: String, newName: String, resultListener: (Profile) -> Unit) {
        db.collection(rootCollection).document(uid).update("name", newName)
            .addOnSuccessListener {
                getProfileByUid(uid, resultListener)
            }
    }

    fun updateProfileBio(uid: String, newBio: String, resultListener: (Profile) -> Unit) {
        db.collection(rootCollection).document(uid).update("bio", newBio)
            .addOnSuccessListener {
                getProfileByUid(uid, resultListener)
            }
    }

    fun updateProfilePicture(uid: String, pictureUuid: String,resultListener: (Profile) -> Unit) {
        db.collection(rootCollection).document(uid).update("profilePictureUuid", pictureUuid)
            .addOnSuccessListener {
                getProfileByUid(uid, resultListener)
            }
    }

    fun updateLevelsCompleted(uid: String, levelsCompleted: Int, resultListener: (Profile) -> Unit) {
        db.collection(rootCollection).document(uid).update("levelsCompleted", levelsCompleted)
            .addOnSuccessListener {
                getProfileByUid(uid, resultListener)
            }
    }
}