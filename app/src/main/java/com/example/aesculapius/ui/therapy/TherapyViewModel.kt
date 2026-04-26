package com.example.aesculapius.ui.therapy

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aesculapius.database.AesculapiusRepository
import com.example.aesculapius.database.UserPreferencesRepository
import com.example.aesculapius.domain.medicine.usecases.AcceptDoseUseCase
import com.example.aesculapius.domain.medicine.usecases.AddMedicineUseCase
import com.example.aesculapius.domain.medicine.usecases.DeleteMedicineUseCase
import com.example.aesculapius.domain.medicine.usecases.GetMedicinesForDateUseCase
import com.example.aesculapius.domain.medicine.usecases.SkipDoseUseCase
import com.example.aesculapius.domain.medicine.usecases.UpdateMedicineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.boguszpawlowski.composecalendar.week.Week
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TherapyViewModel @Inject constructor(
    private val getMedicinesForDateUseCase: GetMedicinesForDateUseCase,
    private val addMedicineUseCase: AddMedicineUseCase,
    private val updateMedicineUseCase: UpdateMedicineUseCase,
    private val deleteMedicineUseCase: DeleteMedicineUseCase,
    private val acceptDoseUseCase: AcceptDoseUseCase,
    private val skipDoseUseCase: SkipDoseUseCase,
    private val aesculapiusRepository: AesculapiusRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    /** [currentDate] - при изменении выбранного дня, меняется currentDate вместе с state, который отвечает за отображение
     * дня в календаре. При изменении currentDate внутри списка не происходит recomposition,
     * таким образом данная "уловка" позволяет не отрисовывать календарь дважды, а только получать
     * текущую дату, когда это необходимо для отображения лекарств */
    private val currentDate = mutableStateListOf(LocalDate.now())

    /** [currentLoadingState] статус загрузки статус бара и писка препаратов */
    var currentLoadingState: MutableStateFlow<CurrentLoadingState> = MutableStateFlow(CurrentLoadingState.Loading)
    /** [generalLoadingState] статус загрузки всего содержимого экрана терапии */
    var generalLoadingState: MutableStateFlow<GeneralLoadingState> = MutableStateFlow(GeneralLoadingState.Success)

    private var _currentWeekDates = MutableStateFlow(Week.now())
    val currentWeekDates: StateFlow<Week> = _currentWeekDates

    private var _isWeek = MutableStateFlow(true)
    val isWeek: StateFlow<Boolean> = _isWeek

    init { updateCurrentDate(LocalDate.now()) }

    fun onTherapyEvent(event: TherapyEvent) {
        when (event) {
            is TherapyEvent.OnAcceptMedicine -> {
                viewModelScope.launch {
                    generalLoadingState.value = GeneralLoadingState.Loading
                    val userId = userPreferencesRepository.user.first().id ?: ""
                    acceptDoseUseCase(userId, event.idDose)
                    updateCurrentDate(LocalDate.now())
                    generalLoadingState.value = GeneralLoadingState.Success
                }
            }

            is TherapyEvent.OnSkipMedicine -> {
                viewModelScope.launch {
                    generalLoadingState.value = GeneralLoadingState.Loading
                    val userId = userPreferencesRepository.user.first().id ?: ""
                    skipDoseUseCase(userId, event.idDose)
                    updateCurrentDate(LocalDate.now())
                    generalLoadingState.value = GeneralLoadingState.Success
                }
            }

            is TherapyEvent.OnChangeIsWeek -> {
                _isWeek.value = event.isWeek
            }

            is TherapyEvent.OnAddMedicineItem -> {
                viewModelScope.launch {
                    generalLoadingState.value = GeneralLoadingState.Loading
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
                    if (!currentDate.first().isBefore(LocalDate.now()))
                        updateCurrentDate(getCurrentDate())
                    generalLoadingState.value = GeneralLoadingState.Success
                }
            }

            is TherapyEvent.OnUpdateMedicineItem -> {
                viewModelScope.launch {
                    generalLoadingState.value = GeneralLoadingState.Loading
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
                    updateCurrentDate(LocalDate.now())
                    generalLoadingState.value = GeneralLoadingState.Success
                }
            }

            is TherapyEvent.OnDeleteMedicineItem -> {
                viewModelScope.launch {
                    generalLoadingState.value = GeneralLoadingState.Loading
                    val userId = userPreferencesRepository.user.first().id ?: ""
                    deleteMedicineUseCase(userId, event.medicineId)
                    updateCurrentDate(LocalDate.now())
                    generalLoadingState.value = GeneralLoadingState.Success
                }
            }

            is TherapyEvent.OnGetWeekDates -> {
                val weekDates = ArrayList<LocalDate>()
                var startOfWeek = event.currentDate
                while (startOfWeek.dayOfWeek != DayOfWeek.MONDAY) {
                    startOfWeek = startOfWeek.minusDays(1)
                }
                for (i in 0 until 7) {
                    weekDates.add(startOfWeek.plusDays(i.toLong()))
                }
                _currentWeekDates.update { Week(weekDates) }
            }
        }
    }

    /** [updateCurrentDate] - при обновлении даты требует обновить список лекарств к употреблению
     * в это время (связано с работой с данными, поэтому выполняется в фоне, чтобы не блокировать ui) */
    fun updateCurrentDate(newDate: LocalDate): Boolean {
        viewModelScope.launch {
            currentLoadingState.value = CurrentLoadingState.Loading
            currentDate[0] = newDate

            var amountDone = 0
            val medicines = getMedicinesForDateUseCase(newDate).toMutableList()
            val morningMedicines = mutableListOf<MedicineCard>()
            val eveningMedicines = mutableListOf<MedicineCard>()
            medicines.forEach { medicineWithDoses ->
                medicineWithDoses.doses.forEach { dose ->
                    if (dose.isAccepted && dose.date == newDate) amountDone++
                    if (dose.isMorning && dose.date == newDate)
                        morningMedicines.add(
                            MedicineCard(
                                id = medicineWithDoses.medicine.idMedicine,
                                dose = medicineWithDoses.medicine.dose,
                                frequency = dose.dosesAmount,
                                isAccepted = dose.isAccepted,
                                isSkipped = dose.isSkipped || dose.date.isBefore(LocalDate.now()),
                                name = medicineWithDoses.medicine.name,
                                undername = medicineWithDoses.medicine.undername,
                                startDate = medicineWithDoses.medicine.startDate,
                                endDate = medicineWithDoses.medicine.endDate,
                                medicineType = medicineWithDoses.medicine.medicineType,
                                doseId = dose.idDose,
                                fullFrequency = medicineWithDoses.medicine.frequency
                            )
                        )
                    else if (!dose.isMorning && dose.date == newDate)
                        eveningMedicines.add(
                            MedicineCard(
                                id = medicineWithDoses.medicine.idMedicine,
                                dose = medicineWithDoses.medicine.dose,
                                frequency = dose.dosesAmount,
                                isAccepted = dose.isAccepted,
                                isSkipped = dose.isSkipped || dose.date.isBefore(LocalDate.now()),
                                name = medicineWithDoses.medicine.name,
                                undername = medicineWithDoses.medicine.undername,
                                startDate = medicineWithDoses.medicine.startDate,
                                endDate = medicineWithDoses.medicine.endDate,
                                medicineType = medicineWithDoses.medicine.medicineType,
                                doseId = dose.idDose,
                                fullFrequency = medicineWithDoses.medicine.frequency
                            )
                        )
                }
            }

            val result = TherapyUiState(
                currentMorningMedicines = morningMedicines,
                currentEveningMedicines = eveningMedicines,
                done = amountDone,
                amount = morningMedicines.size + eveningMedicines.size,
                progress = amountDone.toFloat() / (morningMedicines.size + eveningMedicines.size)
            )
            currentLoadingState.value = CurrentLoadingState.Success(result)
        }
        return true
    }

    /** [getAmountNotAcceptedMedicines] - служит для отображения индикторов под датами
     * (вызывается из LaunchedEffect) */
    suspend fun getAmountNotAcceptedMedicines(date: LocalDate): Int = viewModelScope.async {
        aesculapiusRepository.getAmountNotAcceptedMedicines(date)
    }.await()

    /** [getCurrentDate] получить дату на которой ейчас находится пользователь */
    fun getCurrentDate(): LocalDate {
        return currentDate.first()
    }
}

sealed interface CurrentLoadingState {
    data class Success(val therapyUiState: TherapyUiState) : CurrentLoadingState
    object Loading : CurrentLoadingState
}

sealed interface GeneralLoadingState {
    object Success : GeneralLoadingState
    object Loading : GeneralLoadingState
}
