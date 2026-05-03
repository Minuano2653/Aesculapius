package com.example.aesculapius.domain.symptoms

import com.example.aesculapius.ui.symptoms.SymptomItem
import kotlinx.coroutines.flow.Flow

interface SymptomRepository {
    suspend fun addSymptom(userId: String, name: String)
    suspend fun deleteSymptom(userId: String, symptomId: String)
    suspend fun getSymptoms(): List<SymptomItem>
    fun getSymptomsFlow(): Flow<List<SymptomItem>>
}
