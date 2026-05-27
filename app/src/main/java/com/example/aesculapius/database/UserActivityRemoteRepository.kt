package com.example.aesculapius.database

import com.example.aesculapius.domain.patients.model.ActivitySnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserActivityRemoteRepository @Inject constructor(
    firestore: FirebaseFirestore
) {
    private val usersRef = firestore.collection(USERS_COLLECTION_REF)

    suspend fun updateActivity(userId: String, activity: ActivitySnapshot) {
        if (userId.isEmpty()) return
        usersRef.document(userId).update(
            "activity",
            mapOf(
                "mainScore" to activity.mainScore,
                "astTestScore" to activity.astTestScore,
                "metricsScore" to activity.metricsScore,
                "medicinesScore" to activity.medicinesScore,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }
}
