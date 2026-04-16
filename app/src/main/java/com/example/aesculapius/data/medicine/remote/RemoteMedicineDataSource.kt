package com.example.aesculapius.data.medicine.remote

import com.example.aesculapius.database.USERS_COLLECTION_REF
import com.example.aesculapius.ui.therapy.MedicineItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteMedicineDataSource @Inject constructor(
    firestore: FirebaseFirestore
) {
    private val usersRef = firestore.collection(USERS_COLLECTION_REF)


    private fun medicinesRef(userId: String) =
        usersRef.document(userId).collection("medicines")

    private fun dosesRef(userId: String, medicineId: Int) =
        medicinesRef(userId).document(medicineId.toString()).collection("doses")

    private fun doseDocId(date: LocalDate, isMorning: Boolean) = "${date}_${isMorning}"

    fun addMedicine(userId: String, medicineId: Int, medicineItem: MedicineItem) {
        medicinesRef(userId).document(medicineId.toString()).set(
            hashMapOf(
                "name" to medicineItem.name,
                "undername" to medicineItem.undername,
                "dose" to medicineItem.dose,
                "frequency" to medicineItem.frequency,
                "medicineType" to medicineItem.medicineType.name,
                "startDate" to medicineItem.startDate.toString(),
                "endDate" to medicineItem.endDate.toString()
            )
        )
    }

    suspend fun updateMedicine(userId: String, medicineId: Int, medicineItem: MedicineItem) {
        val docRef = medicinesRef(userId).document(medicineId.toString())
        docRef.set(
            hashMapOf(
                "name" to medicineItem.name,
                "undername" to medicineItem.undername,
                "dose" to medicineItem.dose,
                "frequency" to medicineItem.frequency,
                "medicineType" to medicineItem.medicineType.name,
                "startDate" to medicineItem.startDate.toString(),
                "endDate" to medicineItem.endDate.toString()
            )
        ).await()
        // Удаляем старые дозы пакетом
        val dosesSnapshot = docRef.collection("doses").get().await()
        if (dosesSnapshot.documents.isNotEmpty()) {
            val batch = Firebase.firestore.batch()
            dosesSnapshot.documents.forEach { batch.delete(it.reference) }
            batch.commit().await()
        }
    }

    suspend fun deleteMedicine(userId: String, medicineId: Int) {
        val docRef = medicinesRef(userId).document(medicineId.toString())
        val dosesSnapshot = docRef.collection("doses").get().await()
        if (dosesSnapshot.documents.isNotEmpty()) {
            val batch = Firebase.firestore.batch()
            dosesSnapshot.documents.forEach { batch.delete(it.reference) }
            batch.commit().await()
        }
        docRef.delete().await()
    }

    fun acceptDose(userId: String, medicineId: Int, date: LocalDate, isMorning: Boolean, dosesAmount: String) {
        dosesRef(userId, medicineId).document(doseDocId(date, isMorning)).set(
            hashMapOf(
                "date" to date.toString(),
                "isMorning" to isMorning,
                "dosesAmount" to dosesAmount,
                "isAccepted" to true,
                "isSkipped" to false
            )
        )
    }

    fun skipDose(userId: String, medicineId: Int, date: LocalDate, isMorning: Boolean, dosesAmount: String) {
        dosesRef(userId, medicineId).document(doseDocId(date, isMorning)).set(
            hashMapOf(
                "date" to date.toString(),
                "isMorning" to isMorning,
                "dosesAmount" to dosesAmount,
                "isAccepted" to false,
                "isSkipped" to true
            )
        )
    }
}
