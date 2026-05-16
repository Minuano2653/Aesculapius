package com.example.aesculapius.domain.auth.usecases

import com.example.aesculapius.database.UserAuthRepository
import com.example.aesculapius.database.UserPreferencesRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: UserAuthRepository,
    private val prefRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke() {
        authRepository.signOut()
        prefRepository.clearUserData()
    }
}
