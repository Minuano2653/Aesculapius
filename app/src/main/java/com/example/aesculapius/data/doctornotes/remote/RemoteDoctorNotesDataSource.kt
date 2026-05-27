package com.example.aesculapius.data.doctornotes.remote

import com.example.aesculapius.database.USERS_COLLECTION_REF
import com.example.aesculapius.domain.doctornotes.model.DoctorNote
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDoctorNotesDataSource @Inject constructor(
    firestore: FirebaseFirestore
) {
    private val usersRef = firestore.collection(USERS_COLLECTION_REF)

    private fun notesRef(patientId: String) =
        usersRef.document(patientId).collection("doctorNotes")

    fun observeNotes(patientId: String, doctorId: String): Flow<List<DoctorNote>> = callbackFlow {
        val registration = notesRef(patientId)
            .whereEqualTo("doctorId", doctorId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents.orEmpty().mapNotNull { doc ->
                    val text = doc["text"] as? String ?: return@mapNotNull null
                    DoctorNote(
                        id = doc.id,
                        doctorId = (doc["doctorId"] as? String).orEmpty(),
                        text = text,
                        createdAtMillis = (doc["createdAt"] as? Timestamp)?.toDate()?.time ?: 0L
                    )
                }
                trySend(items)
            }
        awaitClose { registration.remove() }
    }

    suspend fun addNote(patientId: String, doctorId: String, text: String) {
        notesRef(patientId).add(
            mapOf(
                "doctorId" to doctorId,
                "text" to text,
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    suspend fun deleteNote(patientId: String, noteId: String) {
        notesRef(patientId).document(noteId).delete().await()
    }
}
