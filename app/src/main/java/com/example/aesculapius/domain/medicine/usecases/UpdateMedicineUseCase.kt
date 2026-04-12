package com.example.aesculapius.domain.medicine.usecases

import com.example.aesculapius.data.CurrentMedicineType
import com.example.aesculapius.domain.medicine.MedicineRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateMedicineUseCase @Inject constructor(private val repository: MedicineRepository) {

    suspend operator fun invoke(
        userId: String,
        medicineId: Int,
        frequency: String,
        dose: String,
        medicineType: CurrentMedicineType,
        startDate: LocalDate,
        endDate: LocalDate
    ) = repository.updateMedicine(userId, medicineId, frequency, dose, medicineType, startDate, endDate)
}
