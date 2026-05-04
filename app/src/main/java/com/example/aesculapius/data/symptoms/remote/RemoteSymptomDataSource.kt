package com.example.aesculapius.data.symptoms.remote

import com.example.aesculapius.database.USERS_COLLECTION_REF
import com.example.aesculapius.ui.symptoms.SymptomItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteSymptomDataSource @Inject constructor(
    firestore: FirebaseFirestore
) {
    private val usersRef = firestore.collection(USERS_COLLECTION_REF)

    private fun symptomsRef(userId: String) =
        usersRef.document(userId).collection("symptoms")

    fun addSymptom(userId: String, id: String, name: String, createdAt: String) {
        symptomsRef(userId).document(id).set(
            hashMapOf(
                "name" to name,
                "createdAt" to createdAt
            )
        )
    }

    suspend fun deleteSymptom(userId: String, id: String) {
        symptomsRef(userId).document(id).delete().await()
    }

    suspend fun getAllSymptoms(userId: String): List<SymptomItem> {
        return symptomsRef(userId).get().await().documents.mapNotNull { doc ->
            SymptomItem(
                id = doc.id,
                name = doc.getString("name") ?: return@mapNotNull null,
                createdAt = doc.getString("createdAt") ?: ""
            )
        }
    }
}
