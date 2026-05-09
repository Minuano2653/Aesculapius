package com.example.aesculapius.di

import com.example.aesculapius.data.pdf.PdfRepositoryImpl
import com.example.aesculapius.domain.pdf.PdfRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PdfModule {

    @Binds
    @Singleton
    abstract fun bindPdfRepository(impl: PdfRepositoryImpl): PdfRepository
}
