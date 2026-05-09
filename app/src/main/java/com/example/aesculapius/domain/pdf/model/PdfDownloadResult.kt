package com.example.aesculapius.domain.pdf.model

sealed interface PdfDownloadResult {

    class Success(val bytes: ByteArray) : PdfDownloadResult {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Success) return false
            return bytes.contentEquals(other.bytes)
        }

        override fun hashCode(): Int = bytes.contentHashCode()
    }

    sealed interface Error : PdfDownloadResult {
        data object Unauthorized : Error
        data object UserNotFound : Error
        data object InvalidParams : Error
        data object Network : Error
        data class Unknown(val code: Int, val message: String?) : Error
    }
}
