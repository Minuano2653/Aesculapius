package com.example.aesculapius.domain.diary.usecases

import com.example.aesculapius.domain.diary.DiaryRepository
import com.example.aesculapius.ui.diary.DiaryEntryItem
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetDiaryEntryByDateUseCase @Inject constructor(
    private val repository: DiaryRepository
) {
    suspend operator fun invoke(date: LocalDate): DiaryEntryItem? =
        repository.getEntryByDate(date)
}
