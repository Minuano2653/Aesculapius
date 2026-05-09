package com.example.aesculapius.data.pdf.remote

import com.example.aesculapius.data.pdf.remote.dto.HealthDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PdfApi {

    @GET("health")
    suspend fun health(): Response<HealthDto>

    @GET("report")
    suspend fun downloadReport(
        @Header("Authorization") bearer: String,
        @Query("psvDays") psvDays: Int,
        @Query("astDays") astDays: Int? = null
    ): Response<ResponseBody>
}
