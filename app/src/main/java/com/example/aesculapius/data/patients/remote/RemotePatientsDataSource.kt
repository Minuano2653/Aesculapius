package com.example.aesculapius.data.patients.remote

import com.example.aesculapius.database.USERS_COLLECTION_REF
import com.example.aesculapius.domain.patients.model.ActivitySnapshot
import com.example.aesculapius.domain.patients.model.PatientFull
import com.example.aesculapius.domain.patients.model.PatientSummary
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemotePatientsDataSource @Inject constructor(
    firestore: FirebaseFirestore
) {
    private val usersRef = firestore.collection(USERS_COLLECTION_REF)

    suspend fun getAllPatients(): List<PatientSummary> {
        val snapshot = usersRef.get().await()
        return snapshot.documents.map { doc ->
            PatientSummary(
                id = doc.id,
                name = (doc["name"] as? String).orEmpty(),
                surname = (doc["surname"] as? String).orEmpty(),
                patronymic = (doc["patronymic"] as? String).orEmpty(),
                activity = doc.readActivity()
            )
        }
    }

    suspend fun getPatientFull(uid: String): PatientFull? {
        if (uid.isEmpty()) return null
        val doc = usersRef.document(uid).get().await()
        if (!doc.exists()) return null
        val birthdayStr = doc["birthDate"] as? String
        val birthday = birthdayStr?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        return PatientFull(
            id = doc.id,
            name = (doc["name"] as? String).orEmpty(),
            surname = (doc["surname"] as? String).orEmpty(),
            patronymic = (doc["patronymic"] as? String).orEmpty(),
            birthday = birthday,
            height = (doc["height"] as? Double)?.toFloat() ?: 0f,
            weight = (doc["weight"] as? Double)?.toFloat() ?: 0f,
            activity = doc.readActivity()
        )
    }

    private fun DocumentSnapshot.readActivity(): ActivitySnapshot? {
        @Suppress("UNCHECKED_CAST")
        val activityMap = get("activity") as? Map<String, Any?> ?: return null
        val main = (activityMap["mainScore"] as? Number)?.toDouble() ?: return null
        return ActivitySnapshot(
            mainScore = main,
            astTestScore = (activityMap["astTestScore"] as? Number)?.toDouble() ?: 0.0,
            metricsScore = (activityMap["metricsScore"] as? Number)?.toDouble() ?: 0.0,
            medicinesScore = (activityMap["medicinesScore"] as? Number)?.toDouble() ?: 0.0,
            updatedAtMillis = (activityMap["updatedAt"] as? Timestamp)?.toDate()?.time
        )
    }
}
