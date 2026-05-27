package com.example.aesculapius.domain.profile.usecases

import com.example.aesculapius.database.UserActivityRemoteRepository
import com.example.aesculapius.domain.patients.model.ActivitySnapshot
import com.example.aesculapius.domain.profile.UserActivityResult
import javax.inject.Inject

class UpdateActivityScoreUseCase @Inject constructor(
    private val getUserActivityScoreUseCase: GetUserActivityScoreUseCase,
    private val getUserRegisterDateUseCase: GetUserRegisterDateUseCase,
    private val activityRepository: UserActivityRemoteRepository
) {
    suspend operator fun invoke(userId: String) {
        if (userId.isEmpty()) return
        val result = getUserActivityScoreUseCase(getUserRegisterDateUseCase())
        if (result == UserActivityResult() || result.mainScore < 0) return
        activityRepository.updateActivity(
            userId = userId,
            activity = ActivitySnapshot(
                mainScore = result.mainScore,
                astTestScore = result.astTestScore,
                metricsScore = result.metricsScore,
                medicinesScore = result.medicinesScore
            )
        )
    }
}
