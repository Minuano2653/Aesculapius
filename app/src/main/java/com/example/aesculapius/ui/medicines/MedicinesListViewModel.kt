package com.example.aesculapius.ui.medicines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aesculapius.database.UserPreferencesRepository
import com.example.aesculapius.domain.medicine.usecases.AddMedicineUseCase
import com.example.aesculapius.domain.medicine.usecases.DeleteMedicineUseCase
import com.example.aesculapius.domain.medicine.usecases.GetAllMedicinesUseCase
import com.example.aesculapius.domain.medicine.usecases.UpdateMedicineUseCase
import com.example.aesculapius.ui.therapy.MedicineItem
import com.example.aesculapius.ui.therapy.TherapyEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicinesListViewModel @Inject constructor(
    private val addMedicineUseCase: AddMedicineUseCase,
    private val updateMedicineUseCase: UpdateMedicineUseCase,
    private val deleteMedicineUseCase: DeleteMedicineUseCase,
    private val getAllMedicinesUseCase: GetAllMedicinesUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _medicines = MutableStateFlow<List<MedicineItem>>(emptyList())
    val medicines: StateFlow<List<MedicineItem>> = _medicines

    init {
        loadMedicines()
    }

    fun loadMedicines() {
        viewModelScope.launch {
            _medicines.value = getAllMedicinesUseCase()
        }
    }

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
                    loadMedicines()
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
                    loadMedicines()
                }
            }

            is MedicineEvent.OnDeleteMedicineItem -> {
                viewModelScope.launch {
                    val userId = userPreferencesRepository.user.first().id ?: ""
                    deleteMedicineUseCase(userId, event.medicineId)
                    loadMedicines()
                }
            }
        }
    }
}
