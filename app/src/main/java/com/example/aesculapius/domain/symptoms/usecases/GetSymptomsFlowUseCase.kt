package com.example.aesculapius.domain.symptoms.usecases

import com.example.aesculapius.domain.symptoms.SymptomRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSymptomsFlowUseCase @Inject constructor(private val repository: SymptomRepository) {
    operator fun invoke() = repository.getSymptomsFlow()
}
