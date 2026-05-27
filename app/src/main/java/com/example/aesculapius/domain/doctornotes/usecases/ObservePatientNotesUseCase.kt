package com.example.aesculapius.domain.doctornotes.usecases

import com.example.aesculapius.data.doctornotes.remote.RemoteDoctorNotesDataSource
import com.example.aesculapius.domain.doctornotes.model.DoctorNote
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePatientNotesUseCase @Inject constructor(
    private val dataSource: RemoteDoctorNotesDataSource
) {
    operator fun invoke(patientId: String, doctorId: String): Flow<List<DoctorNote>> =
        dataSource.observeNotes(patientId, doctorId)
}
