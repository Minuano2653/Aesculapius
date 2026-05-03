package com.example.aesculapius.domain.diary.usecases

import com.example.aesculapius.domain.diary.DiaryRepository
import com.example.aesculapius.ui.diary.DiaryEntryItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveDiaryEntryUseCase @Inject constructor(
    private val repository: DiaryRepository
) {
    suspend operator fun invoke(userId: String, entry: DiaryEntryItem) =
        repository.saveEntry(userId, entry)
}
