package com.example.aesculapius.domain.doctornotes.usecases

import com.example.aesculapius.data.doctornotes.remote.RemoteDoctorNotesDataSource
import javax.inject.Inject

class AddDoctorNoteUseCase @Inject constructor(
    private val dataSource: RemoteDoctorNotesDataSource
) {
    suspend operator fun invoke(patientId: String, doctorId: String, text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        dataSource.addNote(patientId, doctorId, trimmed)
    }
}
