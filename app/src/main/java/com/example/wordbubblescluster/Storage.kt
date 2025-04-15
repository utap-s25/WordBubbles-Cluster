package com.example.wordbubblescluster

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Storage {
    // Create a storage reference from our app
    private val photoStorage: StorageReference =
        FirebaseStorage.getInstance().reference.child("images")

    // https://firebase.google.com/docs/storage/android/upload-files#upload_from_a_local_file
    fun uploadImage(uri: Uri, uid: String, imageUuid: String, uploadSuccess: ()->Unit) {
        val uploadTask = photoStorage.child(uid).child(imageUuid).putFile(uri)
        // Register observers to listen for when the download is done or if it fails
        uploadTask
            .addOnSuccessListener {
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                uploadSuccess()
            }
    }
    // https://firebase.google.com/docs/storage/android/delete-files#delete_a_file
    fun deleteImage(uid: String, imageUuid: String, onComplete: ()->Unit) {
        // Delete the file
        photoStorage.child(uid).child(imageUuid).delete()
            .addOnCompleteListener {
                onComplete()
            }
    }

    fun uuid2StorageReference(uid: String, imageUuid: String): StorageReference {
        return photoStorage.child(uid).child(imageUuid)
    }
}