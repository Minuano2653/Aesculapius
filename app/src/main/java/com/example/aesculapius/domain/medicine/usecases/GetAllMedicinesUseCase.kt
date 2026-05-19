package com.example.aesculapius.domain.medicine.usecases

import com.example.aesculapius.domain.medicine.MedicineRepository
import com.example.aesculapius.ui.therapy.MedicineItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllMedicinesUseCase @Inject constructor(private val repository: MedicineRepository) {

    operator fun invoke(): Flow<List<MedicineItem>> = repository.getAllMedicinesFlow()
}
