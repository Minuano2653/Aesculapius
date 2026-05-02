package com.example.aesculapius.domain.symptoms.usecases

import com.example.aesculapius.domain.symptoms.SymptomRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteSymptomUseCase @Inject constructor(private val repository: SymptomRepository) {
    suspend operator fun invoke(userId: String, symptomId: String) =
        repository.deleteSymptom(userId, symptomId)
}
