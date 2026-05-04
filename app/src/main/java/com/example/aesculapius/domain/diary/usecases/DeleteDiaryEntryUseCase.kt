package com.example.aesculapius.domain.diary.usecases

import com.example.aesculapius.domain.diary.DiaryRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteDiaryEntryUseCase @Inject constructor(
    private val repository: DiaryRepository
) {
    suspend operator fun invoke(userId: String, date: LocalDate) =
        repository.deleteEntry(userId, date)
}
