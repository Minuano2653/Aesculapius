package com.example.aesculapius.database

import com.example.aesculapius.ui.signup.DoctorUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

const val DOCTORS_COLLECTION_REF = "doctors"

@Singleton
class DoctorRemoteDataRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    firestore: FirebaseFirestore
) {
    private val doctorsRef = firestore.collection(DOCTORS_COLLECTION_REF)

    suspend fun isDoctor(uid: String): Boolean {
        if (uid.isEmpty()) return false
        return runCatching { doctorsRef.document(uid).get().await().exists() }.getOrDefault(false)
    }

    suspend fun pullDoctor(uid: String): DoctorUiState {
        if (uid.isEmpty()) return DoctorUiState()
        val doc = doctorsRef.document(uid).get().await()
        return DoctorUiState(
            id = uid,
            name = (doc["name"] as? String).orEmpty(),
            surname = (doc["surname"] as? String).orEmpty(),
            patronymic = (doc["patronymic"] as? String).orEmpty(),
            email = (doc["email"] as? String) ?: firebaseAuth.currentUser?.email.orEmpty()
        )
    }
}
