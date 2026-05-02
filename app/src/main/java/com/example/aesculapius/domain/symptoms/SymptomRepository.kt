package com.example.aesculapius.domain.symptoms

import com.example.aesculapius.ui.symptoms.SymptomItem

interface SymptomRepository {
    suspend fun addSymptom(userId: String, name: String)
    suspend fun deleteSymptom(userId: String, symptomId: String)
    suspend fun getSymptoms(): List<SymptomItem>
}
