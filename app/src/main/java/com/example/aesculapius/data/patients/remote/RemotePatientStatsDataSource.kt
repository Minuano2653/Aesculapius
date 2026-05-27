package com.example.aesculapius.data.patients.remote

import com.example.aesculapius.data.tests.remote.RemoteTestDataSource
import com.example.aesculapius.database.USERS_COLLECTION_REF
import com.example.aesculapius.ui.tests.MetricsItem
import com.example.aesculapius.ui.tests.ScoreItem
import com.example.aesculapius.ui.therapy.MedicineItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemotePatientStatsDataSource @Inject constructor(
    private val remoteTestDataSource: RemoteTestDataSource,
    firestore: FirebaseFirestore
) {
    private val usersRef = firestore.collection(USERS_COLLECTION_REF)

    suspend fun getAllAstResults(patientId: String): List<ScoreItem> =
        remoteTestDataSource.fetchAstTests(patientId).sortedBy { it.date }

    suspend fun getMetricsInRange(
        patientId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<MetricsItem> {
        return remoteTestDataSource.fetchPeakFlow(patientId)
            .filter { !it.date.isBefore(startDate) && !it.date.isAfter(endDate) }
            .sortedBy { it.date }
    }

    suspend fun getLinePointsAmount(
        patientId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Int = getMetricsInRange(patientId, startDate, endDate).size

    suspend fun getColumnPointsAmount(
        patientId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Int = remoteTestDataSource.fetchAstTests(patientId)
        .count { !it.date.isBefore(startDate) && !it.date.isAfter(endDate) }

    suspend fun getMedicines(patientId: String): List<MedicineItem> {
        if (patientId.isEmpty()) return emptyList()
        val snapshot = usersRef.document(patientId).collection("medicines").get().await()
        return snapshot.documents.mapNotNull { doc ->
            val id = doc.id.toIntOrNull() ?: return@mapNotNull null
            val medicineTypeStr = (doc["medicineType"] as? String) ?: "Aerosol"
            val medicineType = runCatching {
                com.example.aesculapius.data.CurrentMedicineType.valueOf(medicineTypeStr)
            }.getOrElse { com.example.aesculapius.data.CurrentMedicineType.Aerosol }
            MedicineItem(
                idMedicine = id,
                medicineType = medicineType,
                name = (doc["name"] as? String).orEmpty(),
                undername = (doc["undername"] as? String).orEmpty(),
                dose = (doc["dose"] as? String).orEmpty(),
                frequency = (doc["frequency"] as? String).orEmpty(),
                startDate = runCatching { LocalDate.parse(doc["startDate"] as? String) }
                    .getOrElse { LocalDate.now() },
                endDate = runCatching { LocalDate.parse(doc["endDate"] as? String) }
                    .getOrElse { LocalDate.now() }
            )
        }
    }
}
