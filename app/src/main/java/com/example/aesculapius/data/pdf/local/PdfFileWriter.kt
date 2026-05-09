package com.example.aesculapius.data.pdf.local

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfFileWriter @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun writeReport(bytes: ByteArray): Uri {
        val dir = File(context.cacheDir, REPORTS_SUBDIR).apply { mkdirs() }
        val file = File(dir, "report_${System.currentTimeMillis()}.pdf")
        file.outputStream().use { it.write(bytes) }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    private companion object {
        const val REPORTS_SUBDIR = "reports"
    }
}
