package com.example.aesculapius.di

import com.example.aesculapius.data.symptoms.SymptomRepositoryImpl
import com.example.aesculapius.domain.symptoms.SymptomRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SymptomModule {

    @Binds
    @Singleton
    abstract fun bindSymptomRepository(impl: SymptomRepositoryImpl): SymptomRepository
}
