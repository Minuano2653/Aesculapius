package com.example.aesculapius.domain.doctornotes.usecases

import com.example.aesculapius.data.doctornotes.remote.RemoteDoctorNotesDataSource
import javax.inject.Inject

class DeleteDoctorNoteUseCase @Inject constructor(
    private val dataSource: RemoteDoctorNotesDataSource
) {
    suspend operator fun invoke(patientId: String, noteId: String) {
        dataSource.deleteNote(patientId, noteId)
    }
}
