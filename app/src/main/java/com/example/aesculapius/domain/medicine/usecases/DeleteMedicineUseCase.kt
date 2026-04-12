package com.example.aesculapius.domain.medicine.usecases

import com.example.aesculapius.domain.medicine.MedicineRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteMedicineUseCase @Inject constructor(private val repository: MedicineRepository) {

    suspend operator fun invoke(userId: String, medicineId: Int) =
        repository.deleteMedicine(userId, medicineId)
}
