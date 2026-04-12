package com.example.aesculapius.domain.medicine.usecases

import com.example.aesculapius.domain.medicine.MedicineRepository
import com.example.aesculapius.ui.therapy.MedicineItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllMedicinesUseCase @Inject constructor(private val repository: MedicineRepository) {

    suspend operator fun invoke(): List<MedicineItem> = repository.getAllMedicines()
}
