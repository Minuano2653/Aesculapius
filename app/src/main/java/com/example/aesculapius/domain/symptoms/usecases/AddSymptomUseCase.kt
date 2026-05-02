package com.example.aesculapius.domain.symptoms.usecases

import com.example.aesculapius.domain.symptoms.SymptomRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddSymptomUseCase @Inject constructor(private val repository: SymptomRepository) {
    suspend operator fun invoke(userId: String, name: String) =
        repository.addSymptom(userId, name)
}
