package com.example.aesculapius.data.diary

import com.example.aesculapius.data.diary.remote.RemoteDiaryDataSource
import com.example.aesculapius.database.ItemDAO
import com.example.aesculapius.domain.diary.DiaryRepository
import com.example.aesculapius.ui.diary.DiaryEntryItem
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiaryRepositoryImpl @Inject constructor(
    private val itemDAO: ItemDAO,
    private val remote: RemoteDiaryDataSource
) : DiaryRepository {

    override suspend fun saveEntry(userId: String, entry: DiaryEntryItem) {
        val existing = itemDAO.getDiaryEntryByDate(entry.date)
        val finalEntry = entry.copy(
            createdAt = existing?.createdAt ?: entry.createdAt
        )
        itemDAO.insertOrReplaceDiaryEntry(finalEntry)
        if (userId.isNotEmpty()) {
            remote.saveEntry(
                userId = userId,
                date = finalEntry.date,
                symptomsByName = parseSymptomsJson(finalEntry.symptomsJson),
                note = finalEntry.note,
                createdAt = finalEntry.createdAt
            )
        }
    }

    override suspend fun deleteEntry(userId: String, date: LocalDate) {
        itemDAO.deleteDiaryEntry(date)
        if (userId.isNotEmpty()) {
            remote.deleteEntry(userId, date)
        }
    }

    override fun getAllEntriesFlow(): Flow<List<DiaryEntryItem>> =
        itemDAO.getAllDiaryEntriesFlow()

    override suspend fun getEntryByDate(date: LocalDate): DiaryEntryItem? =
        itemDAO.getDiaryEntryByDate(date)

    private fun parseSymptomsJson(json: String): Map<String, Int> {
        if (json.isBlank()) return emptyMap()
        val obj = JSONObject(json)
        val result = mutableMapOf<String, Int>()
        val keys = obj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            result[key] = obj.getInt(key)
        }
        return result
    }
}
