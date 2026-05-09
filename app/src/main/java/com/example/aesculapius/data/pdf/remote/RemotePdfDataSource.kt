package com.example.aesculapius.data.pdf.remote

import android.util.Log
import com.example.aesculapius.domain.pdf.model.PdfDownloadResult
import com.example.aesculapius.domain.pdf.model.PdfHealthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemotePdfDataSource @Inject constructor(
    private val api: PdfApi,
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun checkHealth(): PdfHealthResult = runCatching {
        val response = api.health()
        if (response.isSuccessful && response.body()?.status == "ok") {
            PdfHealthResult.Ok
        } else {
            PdfHealthResult.Down
        }
    }.getOrDefault(PdfHealthResult.Down)

    suspend fun downloadReport(psvDays: Int, astDays: Int?): PdfDownloadResult {
        val user = firebaseAuth.currentUser
            ?: return PdfDownloadResult.Error.Unauthorized
        val token = runCatching { user.getIdToken(false).await().token.also { Log.d("bearer", "$it") } }
            .getOrNull()
            ?: return PdfDownloadResult.Error.Unauthorized

        return try {
            val response = api.downloadReport("Bearer $token", psvDays, astDays)
            if (response.isSuccessful) {
                val body = response.body()
                    ?: return PdfDownloadResult.Error.Unknown(response.code(), "empty body")
                PdfDownloadResult.Success(body.bytes())
            } else {
                mapError(response.code())
            }
        } catch (e: IOException) {
            PdfDownloadResult.Error.Network
        } catch (t: Throwable) {
            PdfDownloadResult.Error.Unknown(-1, t.message)
        }
    }

    private fun mapError(code: Int): PdfDownloadResult.Error = when (code) {
        401 -> PdfDownloadResult.Error.Unauthorized
        404 -> PdfDownloadResult.Error.UserNotFound
        422 -> PdfDownloadResult.Error.InvalidParams
        else -> PdfDownloadResult.Error.Unknown(code, null)
    }
}
