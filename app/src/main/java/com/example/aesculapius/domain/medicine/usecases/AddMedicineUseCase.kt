package com.example.aesculapius.domain.medicine.usecases

import com.example.aesculapius.data.CurrentMedicineType
import com.example.aesculapius.domain.medicine.MedicineRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddMedicineUseCase @Inject constructor(private val repository: MedicineRepository) {

    suspend operator fun invoke(
        userId: String,
        medicineType: CurrentMedicineType,
        name: String,
        undername: String,
        dose: String,
        frequency: String,
        startDate: LocalDate,
        endDate: LocalDate
    ) = repository.addMedicine(userId, medicineType, name, undername, dose, frequency, startDate, endDate)
}
