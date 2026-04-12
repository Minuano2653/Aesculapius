package com.example.aesculapius.domain.medicine.usecases

import com.example.aesculapius.domain.medicine.MedicineRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMedicinesScoreUseCase @Inject constructor(private val repository: MedicineRepository) {

    suspend operator fun invoke(): Double {
        var amountDoses = 0.0
        var acceptedDoses = 0.0
        val startDate = LocalDate.now().minusMonths(1)
        val endDate = LocalDate.now()
        val medicines = repository.getMedicinesInPeriod(startDate, endDate)
        medicines.forEach { medicineWithDoses ->
            medicineWithDoses.doses.forEach {
                if ((it.date.isBefore(endDate) && it.date.isAfter(startDate)) ||
                    (it.date == startDate || it.date == endDate)
                ) {
                    if (it.isAccepted) {
                        if (it.dosesAmount[0] == '1') { acceptedDoses++; amountDoses++ }
                        else if (it.dosesAmount[0] == '2') { acceptedDoses += 2; amountDoses += 2 }
                    } else {
                        if (it.dosesAmount[0] == '1') amountDoses++
                        else if (it.dosesAmount[0] == '2') amountDoses += 2
                    }
                }
            }
        }
        return if (amountDoses != 0.0) acceptedDoses / amountDoses else 0.0
    }
}
