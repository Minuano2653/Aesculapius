package com.example.aesculapius.domain.airquality.usecases

import com.example.aesculapius.domain.airquality.AirQualityRepository
import com.example.aesculapius.domain.airquality.model.AirQualityCache
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSavedAirQualityCacheFlowUseCase @Inject constructor(
    private val repository: AirQualityRepository
) {
    operator fun invoke(): Flow<AirQualityCache?> = repository.observeCache()
}
