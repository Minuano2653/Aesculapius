package com.example.aesculapius.domain.medicine.usecases

import com.example.aesculapius.domain.medicine.MedicineRepository
import com.example.aesculapius.ui.therapy.MedicineWithDoses
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMedicinesForDateUseCase @Inject constructor(private val repository: MedicineRepository) {

    suspend operator fun invoke(date: LocalDate): List<MedicineWithDoses> =
        repository.getMedicinesForDate(date)
}
