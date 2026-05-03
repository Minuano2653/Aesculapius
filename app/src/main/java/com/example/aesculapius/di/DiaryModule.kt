package com.example.aesculapius.di

import com.example.aesculapius.data.diary.DiaryRepositoryImpl
import com.example.aesculapius.domain.diary.DiaryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DiaryModule {

    @Binds
    @Singleton
    abstract fun bindDiaryRepository(impl: DiaryRepositoryImpl): DiaryRepository
}
