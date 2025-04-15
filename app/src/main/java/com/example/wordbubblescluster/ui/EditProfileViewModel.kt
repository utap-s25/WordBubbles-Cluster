package com.example.wordbubblescluster.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditProfileViewModel: ViewModel() {
    private val profilePictureUri = MutableLiveData<Uri?>()
    private val name = MutableLiveData<String>()
    private val bio = MutableLiveData<String>()

    fun setProfilePictureUri(uri: Uri?) {
        profilePictureUri.value = uri
    }

    fun observeProfilePictureUri(): LiveData<Uri?> {
        return profilePictureUri
    }

    fun setName(name: String) {
        this.name.value = name
    }

    fun observeName(): LiveData<String> {
        return name
    }

    fun setBio(bio: String) {
        this.bio.value = bio
    }

    fun observeBio(): LiveData<String> {
        return bio
    }
}