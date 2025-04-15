package com.example.wordbubblescluster.ui

import android.app.Activity
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wordbubblescluster.AuthUser
import com.example.wordbubblescluster.R
import com.example.wordbubblescluster.Storage
import com.example.wordbubblescluster.User
import com.example.wordbubblescluster.ViewModelDBHelper
import com.example.wordbubblescluster.databinding.ActionBarBinding
import com.example.wordbubblescluster.glide.Glide
import com.example.wordbubblescluster.invalidUser
import com.example.wordbubblescluster.model.Profile
import com.example.wordbubblescluster.model.Repository
import java.util.UUID

class MainViewModel: ViewModel() {
    private var title = MutableLiveData<String>()
    private var actionBarBinding: ActionBarBinding? = null
    private lateinit var currentAuthUser: AuthUser
    private var currentUser = MutableLiveData(invalidUser)
    private val profile = MutableLiveData<Profile>()
    private val leaderboard = MutableLiveData<List<Profile>>()

    private var ranking = MediatorLiveData<List<Int>>().apply {
        addSource(leaderboard) { leaderboard ->
            var currentRank = 1
            var currentLowest = leaderboard[0].levelsCompleted
            val newRanking: MutableList<Int> = mutableListOf()
            for (profile in leaderboard) {
                if(profile.levelsCompleted < currentLowest) {
                    currentRank += 1
                    currentLowest = profile.levelsCompleted
                }
                newRanking.add(currentRank)
            }
            value = newRanking
        }
    }

    private val levels = Repository()

    private val dbHelper = ViewModelDBHelper()
    private val storage = Storage()

    fun observeTitle(): LiveData<String> {
        return title
    }

    fun setTitle(newTitle: String) {
        title.value = newTitle
    }

    fun initActionBarBinding(it: ActionBarBinding) {
        actionBarBinding = it
    }

    fun hideActionBarButtons() {
        actionBarBinding?.actionButtonsGroup?.visibility = View.GONE
    }

    fun showActionBarButtons() {
        actionBarBinding?.actionButtonsGroup?.visibility = View.VISIBLE
    }

    fun setCurrentAuthUser(user: AuthUser) {
        currentAuthUser = user
    }

    fun setCurrentUser(user: User) {
        currentUser.value = user
    }

    fun observeCurrentUser(): LiveData<User> {
        return currentUser
    }

    fun login() {
        currentAuthUser.login()
    }

    fun logout() {
        currentAuthUser.logout()
    }

    fun createOrUpdateProfile(newProfile: Profile) {
        dbHelper.createProfile(newProfile) {
            profile.postValue(it)
        }
    }

    fun fetchProfile(uid: String) {
        dbHelper.getProfileByUid(uid) {
            profile.postValue(it)
        }
    }

    fun observeProfile(): LiveData<Profile> {
        return profile
    }

    fun addLeaderboardSnapshotListener(activity: Activity) {
        dbHelper.addLeaderboardSnapshotListener(activity) { profiles ->
            leaderboard.postValue(profiles.sortedByDescending{it})
        }
    }

    fun observeLeaderboard(): LiveData<List<Profile>> {
        return leaderboard
    }

    fun observeRanking(): LiveData<List<Int>> {
        return ranking
    }

    fun setNewName(uid: String, newName: String) {
        dbHelper.updateProfileName(uid, newName) {
            profile.postValue(it)
        }
    }

    fun setNewBio(uid: String, newBio: String) {
        dbHelper.updateProfileBio(uid, newBio) {
            profile.postValue(it)
        }
    }

    fun levelCompleted(uid: String, levelCompleted: Int) {
        profile.value?.let {
            if (levelCompleted > it.levelsCompleted)
                dbHelper.updateLevelsCompleted(uid, levelCompleted) {
                    profile.postValue(it)
                }
        }
    }

    fun setNewProfilePicture(fileUri: Uri, uid: String) {
        val oldImageUuid = profile.value?.profilePictureUuid
        val imageUuid = UUID.randomUUID().toString()
        storage.uploadImage(fileUri, uid, imageUuid) {
            dbHelper.updateProfilePicture(uid, imageUuid) {
                profile.postValue(it)
            }
            if(!oldImageUuid.isNullOrEmpty())
                storage.deleteImage(uid, oldImageUuid) { }
        }
    }

    fun glideFetch(uid: String, imageUuid: String, imageView: ImageView) {
        Glide.fetch(storage.uuid2StorageReference(uid, imageUuid), imageView)
    }

    fun fetchProfilePictureActionBar(uid: String, imageUuid: String) {
        actionBarBinding?.let { glideFetch(uid, imageUuid, it.actionProfile) }
    }

    fun resetProfilePictureActionBar() {
        actionBarBinding?.let {
            it.actionProfile.setImageResource(R.drawable.profile_circle_128)
        }
    }
}
