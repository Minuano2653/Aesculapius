package com.example.aesculapius.domain.diary.usecases

import com.example.aesculapius.domain.diary.DiaryRepository
import com.example.aesculapius.ui.diary.DiaryEntryItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetDiaryEntriesUseCase @Inject constructor(
    private val repository: DiaryRepository
) {
    operator fun invoke(): Flow<List<DiaryEntryItem>> = repository.getAllEntriesFlow()
}
