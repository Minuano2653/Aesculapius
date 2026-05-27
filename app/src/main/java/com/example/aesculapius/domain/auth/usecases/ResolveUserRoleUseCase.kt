package com.example.aesculapius.domain.auth.usecases

import com.example.aesculapius.database.DoctorRemoteDataRepository
import com.example.aesculapius.domain.auth.model.UserRole
import javax.inject.Inject

class ResolveUserRoleUseCase @Inject constructor(
    private val doctorRepository: DoctorRemoteDataRepository
) {
    suspend operator fun invoke(uid: String): UserRole =
        if (doctorRepository.isDoctor(uid)) UserRole.DOCTOR else UserRole.PATIENT
}
