package com.example.aesculapius.domain.diary

import com.example.aesculapius.ui.diary.DiaryEntryItem
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface DiaryRepository {
    suspend fun saveEntry(userId: String, entry: DiaryEntryItem)
    suspend fun deleteEntry(userId: String, date: LocalDate)
    fun getAllEntriesFlow(): Flow<List<DiaryEntryItem>>
    suspend fun getEntryByDate(date: LocalDate): DiaryEntryItem?
}
