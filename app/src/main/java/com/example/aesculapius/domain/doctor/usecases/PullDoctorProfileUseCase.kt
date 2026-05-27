package com.example.aesculapius.domain.doctor.usecases

import com.example.aesculapius.database.DoctorRemoteDataRepository
import com.example.aesculapius.ui.signup.DoctorUiState
import javax.inject.Inject

class PullDoctorProfileUseCase @Inject constructor(
    private val doctorRepository: DoctorRemoteDataRepository
) {
    suspend operator fun invoke(uid: String): DoctorUiState = doctorRepository.pullDoctor(uid)
}
