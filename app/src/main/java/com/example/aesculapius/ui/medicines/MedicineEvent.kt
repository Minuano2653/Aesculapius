package com.example.aesculapius.ui.medicines

import com.example.aesculapius.data.CurrentMedicineType
import com.example.aesculapius.ui.therapy.TherapyEvent
import java.time.LocalDate

sealed interface MedicineEvent {
    data class OnAddMedicineItem(
        val medicineType: CurrentMedicineType,
        val name: String,
        val undername: String,
        val dose: String,
        val frequency: String,
        val startDate: LocalDate,
        val endDate: LocalDate
    ): MedicineEvent

    data class OnUpdateMedicineItem(
        val medicineId: Int,
        val frequency: String,
        val dose: String,
        val medicineType: CurrentMedicineType,
        val startDate: LocalDate,
        val endDate: LocalDate
    ): MedicineEvent

    data class OnDeleteMedicineItem(val medicineId: Int): MedicineEvent
}