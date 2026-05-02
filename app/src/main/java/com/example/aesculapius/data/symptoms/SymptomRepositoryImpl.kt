package com.example.aesculapius.data.symptoms

import com.example.aesculapius.data.symptoms.remote.RemoteSymptomDataSource
import com.example.aesculapius.database.ItemDAO
import com.example.aesculapius.domain.symptoms.SymptomRepository
import com.example.aesculapius.ui.symptoms.SymptomItem
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SymptomRepositoryImpl @Inject constructor(
    private val itemDAO: ItemDAO,
    private val remote: RemoteSymptomDataSource
) : SymptomRepository {

    override suspend fun addSymptom(userId: String, name: String) {
        val id = UUID.randomUUID().toString()
        val createdAt = LocalDateTime.now().toString()
        itemDAO.insertSymptom(id, name, createdAt)
        if (userId.isNotEmpty()) {
            remote.addSymptom(userId, id, name, createdAt)
        }
    }

    override suspend fun deleteSymptom(userId: String, symptomId: String) {
        itemDAO.deleteSymptom(symptomId)
        if (userId.isNotEmpty()) {
            remote.deleteSymptom(userId, symptomId)
        }
    }

    override suspend fun getSymptoms(): List<SymptomItem> = itemDAO.getAllSymptoms()
}
