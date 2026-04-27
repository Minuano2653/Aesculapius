package com.example.aesculapius.di

import com.example.aesculapius.BuildConfig
import com.example.aesculapius.data.airquality.remote.OpenWeatherApi
import com.example.aesculapius.data.airquality.remote.YandexGeocoderApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

private const val OPEN_WEATHER_BASE_URL = "https://api.openweathermap.org/"
private const val GEOCODER_BASE_URL = "https://geocode-maps.yandex.ru/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    @OpenWeatherRetrofit
    fun provideOpenWeatherRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(OPEN_WEATHER_BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    @GeocoderRetrofit
    fun provideGeocoderRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(GEOCODER_BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideOpenWeatherApi(@OpenWeatherRetrofit retrofit: Retrofit): OpenWeatherApi =
        retrofit.create(OpenWeatherApi::class.java)

    @Provides
    @Singleton
    fun provideYandexGeocoderApi(@GeocoderRetrofit retrofit: Retrofit): YandexGeocoderApi =
        retrofit.create(YandexGeocoderApi::class.java)

    @Provides
    @Singleton
    @OpenWeatherApiKey
    fun provideOpenWeatherApiKey(): String = BuildConfig.OPENWEATHER_API_KEY

    @Provides
    @Singleton
    @GeocoderApiKey
    fun provideGeocoderApiKey(): String = BuildConfig.GEOCODER_API_KEY
}
