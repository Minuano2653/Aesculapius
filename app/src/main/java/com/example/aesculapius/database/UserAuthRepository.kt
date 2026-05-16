package com.example.aesculapius.database

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

/** [UserAuthRepository] репозиторий для авторизации и регистрации пользователей через Firebase Auth */
@Singleton
class UserAuthRepository @Inject constructor() {
    companion object {
        val auth = Firebase.auth
    }

    fun login(
        email: String,
        password: String,
        onComplete: (Boolean, FirebaseException, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    onComplete.invoke(true, FirebaseAuthInvalidCredentialsException("ERROR_INVALID_CREDENTIAL", "Invalid credentials provided"), auth.currentUser?.uid.orEmpty())
                else
                    onComplete.invoke(false, it.exception as FirebaseException, auth.currentUser?.uid.orEmpty())
            }
    }

    fun signup(
        email: String,
        password: String,
        onComplete: (Boolean, FirebaseException, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    onComplete(true, FirebaseAuthInvalidCredentialsException("ERROR_INVALID_CREDENTIAL", "Invalid credentials provided"), auth.currentUser?.uid.orEmpty())
                else
                    onComplete(false, it.exception as FirebaseException, auth.currentUser?.uid.orEmpty())
            }
    }

    fun signOut() {
        auth.signOut()
    }

    fun sendPasswordResetEmail(
        email: String,
        onComplete: (Boolean, FirebaseException?) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) onComplete(true, null)
                else onComplete(false, it.exception as? FirebaseException)
            }
    }
}