package com.example.aesculapius.di

import com.example.aesculapius.data.tests.TestRepositoryImpl
import com.example.aesculapius.domain.tests.TestRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TestModule {

    @Binds
    @Singleton
    abstract fun bindTestRepository(impl: TestRepositoryImpl): TestRepository
}
