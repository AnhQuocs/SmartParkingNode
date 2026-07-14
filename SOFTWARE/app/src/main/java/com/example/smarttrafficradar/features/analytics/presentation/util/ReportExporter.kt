package com.example.smarttrafficradar.features.analytics.presentation.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.payment.domain.model.PaymentHistory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object ReportExporter {
    fun exportTransactionsToCsv(context: Context, transactions: List<PaymentHistory>) {
        val fileName = "${context.getString(R.string.report_filename_prefix)}${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)
        
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        
        try {
            file.writer().use { writer ->
                // Header (BOM cho Excel hiển thị đúng tiếng Việt UTF-8)
                writer.write("\uFEFF")
                writer.write("${context.getString(R.string.csv_header)}\n")
                
                transactions.forEach { t ->
                    writer.write("${t.id},${sdf.format(Date(t.createdAt))},${t.amount},${t.method},${t.status}\n")
                }
            }
            
            shareFile(context, file)
        } catch (e: Exception) {
            e.printStackTrace()
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
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.export_report_via)))
    }
}
