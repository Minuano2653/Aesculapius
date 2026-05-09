package com.example.aesculapius.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.work.WorkerFactory
import com.example.aesculapius.database.AesculapiusDatabase
import com.example.aesculapius.database.AesculapiusRepository
import com.example.aesculapius.database.ItemDAO
import com.example.aesculapius.database.UserRemoteDataRepository
import com.example.aesculapius.worker.DaggerWorkerFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.prefs.Preferences
import javax.inject.Singleton

// мы не имеем доступа к исходному коду Room, поэтому создаём модуль, который вернёт нам объект бд
@Module
// данный объект будет доступен любой части приложения
@InstallIn(SingletonComponent::class)
object MainModule {
    // раньше делали при помощи Injection, теерь редачим не конструктор класса, а его объект
    @Provides
    // предоставляет объект бд, один на весь проект (Singleton)
    @Singleton
    // в Dagger по умолчанию есть функция, возвращающая Application, поэтому не возникет проблем
    fun provideAesculapiusDB(application: Application): ItemDAO {
        return AesculapiusDatabase.getDatabase(application.applicationContext).itemDao()
    }

    @Provides
    @Singleton
    fun workerFactory(userRemoteDataRepository: UserRemoteDataRepository): WorkerFactory {
        return DaggerWorkerFactory(userRemoteDataRepository)
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }
}