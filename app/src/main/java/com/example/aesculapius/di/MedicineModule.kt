package com.example.aesculapius.di

import com.example.aesculapius.data.medicine.MedicineRepositoryImpl
import com.example.aesculapius.domain.medicine.MedicineRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MedicineModule {

    @Binds
    @Singleton
    abstract fun bindMedicineRepository(impl: MedicineRepositoryImpl): MedicineRepository
}
