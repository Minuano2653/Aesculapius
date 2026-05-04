package com.example.aesculapius.data.diary.remote

import com.example.aesculapius.database.USERS_COLLECTION_REF
import com.example.aesculapius.ui.diary.DiaryEntryItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDiaryDataSource @Inject constructor(
    firestore: FirebaseFirestore
) {
    private val usersRef = firestore.collection(USERS_COLLECTION_REF)

    private fun diaryRef(userId: String) =
        usersRef.document(userId).collection("diary")

    fun saveEntry(
        userId: String,
        date: LocalDate,
        symptomsByName: Map<String, Int>,
        note: String,
        createdAt: Long
    ) {
        diaryRef(userId).document(date.toString()).set(
            hashMapOf(
                "date" to date.toString(),
                "symptoms" to symptomsByName,
                "note" to note,
                "createdAt" to createdAt
            )
        )
    }

    suspend fun deleteEntry(userId: String, date: LocalDate) {
        diaryRef(userId).document(date.toString()).delete().await()
    }

    suspend fun getAllEntries(userId: String): List<DiaryEntryItem> {
        return diaryRef(userId).get().await().documents.mapNotNull doc@ { doc ->
            val dateStr = doc.getString("date") ?: return@doc null
            val rawMap = (doc["symptoms"] as? Map<*, *>) ?: emptyMap<Any, Any>()
            val sevMap = rawMap.entries.mapNotNull entry@ { (k, v) ->
                val key = k as? String ?: return@entry null
                val value = (v as? Number)?.toInt() ?: return@entry null
                key to value
            }.toMap()
            DiaryEntryItem(
                date = LocalDate.parse(dateStr),
                symptomsJson = JSONObject(sevMap as Map<*, *>).toString(),
                note = doc.getString("note") ?: "",
                createdAt = doc.getLong("createdAt") ?: 0L
            )
        }
    }
}
