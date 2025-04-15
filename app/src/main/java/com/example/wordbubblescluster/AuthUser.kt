package com.example.wordbubblescluster

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

data class User (private val nullableName: String?,
                 private val nullableEmail: String?,
                 val uid: String) {
    val name: String = nullableName ?: ""
    val email: String = nullableEmail ?: ""
}
const val invalidUserUid = "-1"
// Extension function to determine if user is valid
fun User.isInvalid(): Boolean {
    return uid == invalidUserUid
}
val invalidUser = User(null, null,
    invalidUserUid)

class AuthUser(private val registry: ActivityResultRegistry) :
    FirebaseAuth.AuthStateListener,
    DefaultLifecycleObserver {
    companion object {
        private const val TAG = "AuthUser"
    }

    private var pendingLogin = false

    private lateinit var signInLauncher: ActivityResultLauncher<Intent>
    private var liveUser = MutableLiveData<User>().apply {
        this.postValue(invalidUser)
    }
    init {
        Firebase.auth.addAuthStateListener(this)
    }

    fun observeUser(): LiveData<User> {
        return liveUser
    }

    // Active live data upon a change of state for our FirebaseUser
    private fun postUserUpdate(firebaseUser: FirebaseUser?) {
        if(firebaseUser == null) {
            liveUser.postValue(User(null, null, invalidUserUid))
        } else {
            liveUser.postValue(User(firebaseUser.displayName, firebaseUser.email, firebaseUser.uid))
        }
    }
    // This override makes us a valid FirebaseAuth.AuthStateListener
    override fun onAuthStateChanged(p0: FirebaseAuth) {
        postUserUpdate(p0.currentUser)
    }

    override fun onCreate(owner: LifecycleOwner) {
        signInLauncher = registry.register("key", owner,
            FirebaseAuthUIActivityResultContract()) { result ->
            Log.d(TAG, "sign in result ${result.resultCode}")
            // XXX Write me, pendingLogin
            pendingLogin = false
        }
    }
    private fun user(): FirebaseUser? {
        return Firebase.auth.currentUser
    }

    fun login() {
        if (user() == null
            && !pendingLogin
        ) {
            Log.d(TAG, "XXX user null, log in")
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build()
            )

            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.Base_Theme_WordBubblesCluster)
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .build()
            signInLauncher.launch(signInIntent)
            pendingLogin = true
        }
    }
    fun logout() {
        if(user() == null) return
        Firebase.auth.signOut()
    }
}