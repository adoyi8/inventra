package com.sundroid.kobokobo.utils

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.sundroid.kobokobo.dashboard.utils.Util
import com.sundroid.kobokobo.transactionHistory.domain.OrderModel
import com.sundroid.kobokobo.hotel.domain.model.Booking
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
actual fun rememberPdfExporter(): PdfExporter {
    val context = LocalContext.current
    return remember { PdfExporter(context) }
}

actual class PdfExporter(private val context: Context) {
    actual fun exportTransactionPdf(
        businessName: String,
        staffName: String,
        paymentMethod: String,
        dateRange: String,
        pageStr: String,
        transactions: List<OrderModel>
    ): PdfExportResult {
        return try {
            val document = PdfDocument()
            var pageNum = 1
            var pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNum).create()
            var page = document.startPage(pageInfo)
            var canvas = page.canvas

            val titlePaint = Paint().apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = 24f
                color = Color.BLACK
            }
            val textPaint = Paint().apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textSize = 14f
                color = Color.BLACK
            }
            val boldPaint = Paint().apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = 14f
                color = Color.BLACK
            }

            var startY = 50f

            // Header
            canvas.drawText("Business: $businessName", 50f, startY, titlePaint)
            startY += 40f
            
            canvas.drawText("Transaction History", 50f, startY, titlePaint)
            startY += 30f

            canvas.drawText("Filters:", 50f, startY, boldPaint)
            startY += 20f
            canvas.drawText("Staff: $staffName | Payment: $paymentMethod | Date Range: $dateRange | Page: $pageStr", 50f, startY, textPaint)
            startY += 40f

            // Table Header
            val headers = arrayOf("No.", "Order ID", "Date", "Payment", "Amount")
            val xPositions = floatArrayOf(50f, 100f, 250f, 400f, 500f)
            
            for (i in headers.indices) {
                canvas.drawText(headers[i], xPositions[i], startY, boldPaint)
            }
            startY += 10f
            canvas.drawLine(50f, startY, 545f, startY, textPaint)
            startY += 20f

            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

            // Rows
            transactions.forEachIndexed { index, transaction ->
                if (startY > 800f) {
                    document.finishPage(page)
                    pageNum++
                    pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNum).create()
                    page = document.startPage(pageInfo)
                    canvas = page.canvas
                    startY = 50f
                    
                    for (i in headers.indices) {
                        canvas.drawText(headers[i], xPositions[i], startY, boldPaint)
                    }
                    startY += 10f
                    canvas.drawLine(50f, startY, 545f, startY, textPaint)
                    startY += 20f
                }

                val dateString = try { dateFormat.format(Date(transaction.created.toLong())) } catch(e: Exception) { transaction.created }
                val amountStr = Util.currencyFormat(transaction.totalAmount.toDoubleOrNull() ?: 0.0)

                canvas.drawText("${index + 1}", xPositions[0], startY, textPaint)
                canvas.drawText(transaction.orderId.take(15), xPositions[1], startY, textPaint)
                canvas.drawText(dateString, xPositions[2], startY, textPaint)
                canvas.drawText(if (transaction.isPaid) transaction.paymentMethodText.ifEmpty { transaction.paymentMethod } else "Not paid", xPositions[3], startY, textPaint)
                canvas.drawText(amountStr, xPositions[4], startY, textPaint)
                
                startY += 20f

                if (transaction.items.isNotEmpty()) {
                    val itemPaint = Paint().apply {
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                        textSize = 12f
                        color = Color.DKGRAY
                    }
                    transaction.items.forEach { item ->
                        if (startY > 800f) {
                            document.finishPage(page)
                            pageNum++
                            pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNum).create()
                            page = document.startPage(pageInfo)
                            canvas = page.canvas
                            startY = 50f
                            // Headers for new page mid-items
                            for (i in headers.indices) {
                                canvas.drawText(headers[i], xPositions[i], startY, boldPaint)
                            }
                            startY += 10f
                            canvas.drawLine(50f, startY, 545f, startY, textPaint)
                            startY += 20f
                        }
                        
                        val priceStr = Util.currencyFormat(item.price)
                        canvas.drawText("- ${item.itemName} (Qty: ${item.quantity})", xPositions[1] + 10f, startY, itemPaint)
                        canvas.drawText(priceStr, xPositions[4], startY, itemPaint)
                        
                        startY += 15f
                    }
                    startY += 5f
                }
            }

            startY += 10f
            if (startY > 800f) {
                document.finishPage(page)
                pageNum++
                pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNum).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                startY = 50f
            }

            val totalSales = transactions.sumOf { it.totalAmount.replace(",", "").toDoubleOrNull() ?: 0.0 }
            val totalSalesPaint = Paint().apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = 14f
                color = Color.BLACK
                textAlign = Paint.Align.RIGHT
            }
            canvas.drawText("Total Sales: ${Util.currencyFormat(totalSales)}", 545f, startY, totalSalesPaint)

            document.finishPage(page)

            savePdfToStorage(document)
        } catch (e: Exception) {
            e.printStackTrace()
            PdfExportResult(message = "Error rendering PDF: ${e.message}")
        }
    }

    actual fun openPdf(openTarget: String): String? {
        return try {
            val uri = Uri.parse(openTarget)
            val openIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                clipData = ClipData.newRawUri("transaction-history-pdf", uri)
            }
            val chooserIntent = Intent.createChooser(openIntent, "Open PDF").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                clipData = ClipData.newRawUri("transaction-history-pdf", uri)
            }

            context.startActivity(chooserIntent)
            null
        } catch (e: ActivityNotFoundException) {
            "No app found to open PDF files"
        } catch (e: Exception) {
            "Unable to open PDF: ${e.message}"
        }
    }

    actual fun exportBookingHistoryPdf(
        businessName: String,
        dateRange: String,
        staffName: String,
        roomFilter: String,
        paymentMethod: String,
        bookings: List<Booking>
    ): PdfExportResult {
        return try {
            val document = PdfDocument()
            var pageNum = 1
            var pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNum).create()
            var page = document.startPage(pageInfo)
            var canvas = page.canvas

            val titlePaint = Paint().apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = 24f
                color = Color.BLACK
            }
            val textPaint = Paint().apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textSize = 12f
                color = Color.BLACK
            }
            val boldPaint = Paint().apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = 12f
                color = Color.BLACK
            }

            var startY = 50f

            // Header
            canvas.drawText("Business: $businessName", 50f, startY, titlePaint)
            startY += 40f
            canvas.drawText("Booking History", 50f, startY, titlePaint)
            startY += 30f

            canvas.drawText("Filters:", 50f, startY, boldPaint)
            startY += 20f
            canvas.drawText("Staff: $staffName | Room: $roomFilter | Payment: $paymentMethod", 50f, startY, textPaint)
            startY += 18f
            canvas.drawText("Date Range: $dateRange", 50f, startY, textPaint)
            startY += 30f

            // Table Header
            val headers = arrayOf("No.", "Room", "Customer", "Check-in", "Payment", "Amount")
            val xPositions = floatArrayOf(30f, 60f, 140f, 270f, 400f, 500f)

            fun drawHeaders(c: Canvas, y: Float): Float {
                for (i in headers.indices) {
                    c.drawText(headers[i], xPositions[i], y, boldPaint)
                }
                val lineY = y + 8f
                c.drawLine(30f, lineY, 565f, lineY, textPaint)
                return lineY + 15f
            }

            startY = drawHeaders(canvas, startY)

            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

            bookings.forEachIndexed { index, booking ->
                if (startY > 800f) {
                    document.finishPage(page)
                    pageNum++
                    pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNum).create()
                    page = document.startPage(pageInfo)
                    canvas = page.canvas
                    startY = drawHeaders(canvas, 50f)
                }

                val dateString = try { dateFormat.format(Date(booking.checkInTime)) } catch(e: Exception) { "N/A" }
                val amountStr = Util.currencyFormat(booking.totalCost)

                canvas.drawText("${index + 1}", xPositions[0], startY, textPaint)
                canvas.drawText(booking.roomNumber, xPositions[1], startY, textPaint)
                canvas.drawText(booking.customerName.take(15), xPositions[2], startY, textPaint)
                canvas.drawText(dateString, xPositions[3], startY, textPaint)
                canvas.drawText(if (booking.isPaid) booking.paymentMethod else "Not paid", xPositions[4], startY, textPaint)
                canvas.drawText(amountStr, xPositions[5], startY, textPaint)

                startY += 20f
            }

            // Total
            startY += 10f
            if (startY > 800f) {
                document.finishPage(page)
                pageNum++
                pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNum).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                startY = 50f
            }

            val totalRevenue = bookings.sumOf { it.totalCost }
            val totalPaint = Paint().apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = 14f
                color = Color.BLACK
                textAlign = Paint.Align.RIGHT
            }
            canvas.drawText("Total Revenue: ${Util.currencyFormat(totalRevenue)}", 565f, startY, totalPaint)

            document.finishPage(page)
            savePdfToStorage(document, "BookingHistory")
        } catch (e: Exception) {
            e.printStackTrace()
            PdfExportResult(message = "Error rendering PDF: ${e.message}")
        }
    }

    private fun savePdfToStorage(document: PdfDocument, prefix: String = "TransactionHistory"): PdfExportResult {
        val fileName = "${prefix}_${System.currentTimeMillis()}.pdf"
        var outStream: OutputStream? = null

        val path: String
        var openUri: Uri? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                outStream = context.contentResolver.openOutputStream(uri)
                path = "Downloads/$fileName"
                openUri = uri
            } else {
                return PdfExportResult(message = "Failed to create MediaStore entry")
            }
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) downloadsDir.mkdirs()
            val file = File(downloadsDir, fileName)
            outStream = FileOutputStream(file)
            path = file.absolutePath
            openUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        }

        return try {
            outStream.use { stream ->
                document.writeTo(stream)
            }
            document.close()
            PdfExportResult(
                message = "Saved successfully to $path",
                openTarget = openUri?.toString()
            )
        } catch (e: Exception) {
            document.close()
            PdfExportResult(message = "Error saving file: ${e.message}")
        }
    }
}
