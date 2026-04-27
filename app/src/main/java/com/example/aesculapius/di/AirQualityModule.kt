package com.example.aesculapius.di

import android.content.Context
import com.example.aesculapius.data.airquality.AirQualityRepositoryImpl
import com.example.aesculapius.domain.airquality.AirQualityRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AirQualityBindingsModule {

    @Binds
    @Singleton
    abstract fun bindAirQualityRepository(impl: AirQualityRepositoryImpl): AirQualityRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AirQualityProvidersModule {

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
}
