package com.example.aesculapius.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OpenWeatherApiKey

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeocoderApiKey

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OpenWeatherRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeocoderRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PdfRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PdfOkHttp
