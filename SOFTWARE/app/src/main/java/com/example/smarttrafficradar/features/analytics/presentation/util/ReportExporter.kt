package com.example.smarttrafficradar.features.analytics.presentation.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.payment.domain.model.PaymentHistory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportExporter {
    fun exportTransactionsToCsv(context: Context, transactions: List<PaymentHistory>) {
        val fileName =
            "${context.getString(R.string.report_filename_prefix)}${System.currentTimeMillis()}.csv"

        // 1. Tạo nội dung CSV
        val csvContent = StringBuilder()
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

        // Header (BOM cho Excel hiển thị đúng tiếng Việt UTF-8)
        csvContent.append("\uFEFF")

        // Bao header trong dấu ngoặc kép
        val headers = context.getString(R.string.csv_header).split(",")
        csvContent.append(headers.joinToString(",") { "\"$it\"" }).append("\n")

        transactions.forEach { t ->
            val row = listOf(
                t.id,
                sdf.format(Date(t.createdAt)),
                t.amount.toString(),
                t.method,
                t.status
            )
            csvContent.append(row.joinToString(",") { "\"$it\"" }).append("\n")
        }

        // 2. Thực hiện xuất file (Lưu vào Downloads)
        saveFileToDownloads(context, fileName, csvContent.toString())

        // 3. Vẫn giữ lại tính năng Share để người dùng có thêm lựa chọn
        val tempFile = File(context.cacheDir, fileName)
        try {
            tempFile.writeText(csvContent.toString())
            shareFile(context, tempFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveFileToDownloads(context: Context, fileName: String, content: String) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        try {
            val uri =
                resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write(content.toByteArray())
                }
            }

            Toast.makeText(context, context.getString(R.string.export_success), Toast.LENGTH_LONG)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                context,
                context.getString(R.string.export_error, e.message ?: ""),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun shareFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.export_report_via)
            )
        )
    }
}
