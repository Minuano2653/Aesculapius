package com.example.aesculapius.ui.doctor.patientdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aesculapius.domain.doctornotes.model.DoctorNote
import com.example.aesculapius.domain.doctornotes.usecases.AddDoctorNoteUseCase
import com.example.aesculapius.domain.doctornotes.usecases.DeleteDoctorNoteUseCase
import com.example.aesculapius.domain.doctornotes.usecases.ObservePatientNotesUseCase
import com.example.aesculapius.domain.patients.model.PatientFull
import com.example.aesculapius.domain.patients.usecases.GetPatientFullUseCase
import com.example.aesculapius.domain.patients.usecases.GetPatientMedicinesUseCase
import com.example.aesculapius.ui.doctor.navigation.PatientDetailScreen
import com.example.aesculapius.ui.therapy.MedicineItem
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class PatientDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPatientFullUseCase: GetPatientFullUseCase,
    private val getPatientMedicinesUseCase: GetPatientMedicinesUseCase,
    private val addDoctorNoteUseCase: AddDoctorNoteUseCase,
    private val deleteDoctorNoteUseCase: DeleteDoctorNoteUseCase,
    observePatientNotesUseCase: ObservePatientNotesUseCase,
    firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val patientId: String =
        savedStateHandle.get<String>(PatientDetailScreen.patientIdArg).orEmpty()
    private val doctorId: String = firebaseAuth.currentUser?.uid.orEmpty()

    private val _patient = MutableStateFlow<PatientFull?>(null)
    val patient: StateFlow<PatientFull?> = _patient.asStateFlow()

    private val _medicines = MutableStateFlow<List<MedicineItem>>(emptyList())
    val medicines: StateFlow<List<MedicineItem>> = _medicines.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val patientIdFlow = MutableStateFlow(patientId)

    val notes: StateFlow<List<DoctorNote>> = patientIdFlow
        .flatMapLatest { id ->
            if (id.isEmpty() || doctorId.isEmpty()) flowOf(emptyList())
            else observePatientNotesUseCase(id, doctorId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        loadPatient()
    }

    private fun loadPatient() {
        if (patientId.isEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            runCatching { getPatientFullUseCase(patientId) }
                .onSuccess { _patient.value = it }
            runCatching { getPatientMedicinesUseCase(patientId) }
                .onSuccess { _medicines.value = it }
            _isLoading.value = false
        }
    }

    fun addNote(text: String) {
        if (patientId.isEmpty() || doctorId.isEmpty()) return
        viewModelScope.launch {
            runCatching { addDoctorNoteUseCase(patientId, doctorId, text) }
        }
    }

    fun deleteNote(noteId: String) {
        if (patientId.isEmpty()) return
        viewModelScope.launch {
            runCatching { deleteDoctorNoteUseCase(patientId, noteId) }
        }
    }
}
