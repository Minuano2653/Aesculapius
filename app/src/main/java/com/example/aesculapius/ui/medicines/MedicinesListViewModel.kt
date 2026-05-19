package com.example.aesculapius.ui.medicines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aesculapius.database.UserPreferencesRepository
import com.example.aesculapius.domain.medicine.usecases.AddMedicineUseCase
import com.example.aesculapius.domain.medicine.usecases.DeleteMedicineUseCase
import com.example.aesculapius.domain.medicine.usecases.GetAllMedicinesUseCase
import com.example.aesculapius.domain.medicine.usecases.UpdateMedicineUseCase
import com.example.aesculapius.ui.therapy.MedicineItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicinesListViewModel @Inject constructor(
    private val addMedicineUseCase: AddMedicineUseCase,
    private val updateMedicineUseCase: UpdateMedicineUseCase,
    private val deleteMedicineUseCase: DeleteMedicineUseCase,
    getAllMedicinesUseCase: GetAllMedicinesUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val medicines: StateFlow<List<MedicineItem>> = getAllMedicinesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onEvent(event: MedicineEvent) {
        when (event) {
            is MedicineEvent.OnAddMedicineItem -> {
                viewModelScope.launch {
                    val userId = userPreferencesRepository.user.first().id ?: ""
                    addMedicineUseCase(
                        userId,
                        event.medicineType,
                        event.name,
                        event.undername,
                        event.dose,
                        event.frequency,
                        event.startDate,
                        event.endDate
                    )
                }
            }

            is MedicineEvent.OnUpdateMedicineItem -> {
                viewModelScope.launch {
                    val userId = userPreferencesRepository.user.first().id ?: ""
                    updateMedicineUseCase(
                        userId,
                        event.medicineId,
                        event.frequency,
                        event.dose,
                        event.medicineType,
                        event.startDate,
                        event.endDate
                    )
                }
            }

            is MedicineEvent.OnDeleteMedicineItem -> {
                viewModelScope.launch {
                    val userId = userPreferencesRepository.user.first().id ?: ""
                    deleteMedicineUseCase(userId, event.medicineId)
                }
            }
        }
    }
}
