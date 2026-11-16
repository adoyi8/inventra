package com.sundroid.kobokobo.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.lowagie.text.Document
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import com.sundroid.kobokobo.dashboard.utils.Util
import com.sundroid.kobokobo.hotel.domain.model.Booking
import com.sundroid.kobokobo.transactionHistory.domain.OrderModel
import java.awt.Desktop
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
actual fun rememberPdfExporter(): PdfExporter {
    return remember { PdfExporter() }
}

actual class PdfExporter {
    actual fun exportTransactionPdf(
        businessName: String,
        staffName: String,
        paymentMethod: String,
        dateRange: String,
        pageStr: String,
        transactions: List<OrderModel>
    ): PdfExportResult {
        return try {
            val document = Document()
            val fileName = "TransactionHistory_${System.currentTimeMillis()}.pdf"
            val downloadsDir = File(System.getProperty("user.home"), "Downloads")
            if (!downloadsDir.exists()) downloadsDir.mkdirs()
            val file = File(downloadsDir, fileName)

            PdfWriter.getInstance(document, FileOutputStream(file))
            document.open()

            val titleFont = Font(Font.HELVETICA, 18f, Font.BOLD)
            val subFont = Font(Font.HELVETICA, 12f, Font.NORMAL)
            val boldFont = Font(Font.HELVETICA, 12f, Font.BOLD)

            document.add(Paragraph("Business: $businessName", titleFont))
            document.add(Paragraph("Transaction History", titleFont))
            document.add(Paragraph(" "))
            
            document.add(Paragraph("Filters:", boldFont))
            document.add(Paragraph("Staff: $staffName | Payment: $paymentMethod | Date Range: $dateRange | Page: $pageStr", subFont))
            document.add(Paragraph(" "))

            val table = PdfPTable(5)
            table.widthPercentage = 100f

            val headers = arrayOf("No.", "Order ID", "Date", "Payment", "Amount")
            for (header in headers) {
                table.addCell(Paragraph(header, boldFont))
            }

            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

            transactions.forEachIndexed { index, transaction ->
                val dateString = try { dateFormat.format(Date(transaction.created.toLong())) } catch(e: Exception) { transaction.created }
                val amountStr = Util.currencyFormat(transaction.totalAmount.toDoubleOrNull() ?: 0.0)

                table.addCell(Paragraph("${index + 1}", subFont))
                table.addCell(Paragraph(transaction.orderId.take(15), subFont))
                table.addCell(Paragraph(dateString, subFont))
                table.addCell(Paragraph(if (transaction.isPaid) transaction.paymentMethodText.ifEmpty { transaction.paymentMethod } else "Not paid", subFont))
                table.addCell(Paragraph(amountStr, subFont))

                if (transaction.items.isNotEmpty()) {
                    val itemFont = Font(Font.HELVETICA, 10f, Font.ITALIC)
                    val itemsStringBuilder = StringBuilder()
                    transaction.items.forEach { item ->
                        val priceStr = Util.currencyFormat(item.price)
                        itemsStringBuilder.append("      - ${item.itemName} (Qty: ${item.quantity}) - $priceStr\n")
                    }
                    val itemsParagraph = Paragraph(itemsStringBuilder.toString().trimEnd(), itemFont)
                    val itemCell = com.lowagie.text.pdf.PdfPCell(itemsParagraph)
                    itemCell.colspan = 5
                    itemCell.paddingBottom = 8f
                    itemCell.paddingTop = 4f
                    table.addCell(itemCell)
                }
            }

            document.add(table)
            
            document.add(Paragraph(" "))
            val totalSales = transactions.sumOf { it.totalAmount.replace(",", "").toDoubleOrNull() ?: 0.0 }
            val totalParagraph = Paragraph("Total Sales: ${Util.currencyFormat(totalSales)}", boldFont)
            totalParagraph.alignment = com.lowagie.text.Element.ALIGN_RIGHT
            document.add(totalParagraph)

            document.close()

            PdfExportResult(
                message = "Saved successfully to ${file.absolutePath}",
                openTarget = file.absolutePath
            )
        } catch (e: Exception) {
            e.printStackTrace()
            PdfExportResult(message = "Error rendering PDF: ${e.message}")
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
            val document = Document()
            val fileName = "BookingHistory_${System.currentTimeMillis()}.pdf"
            val downloadsDir = File(System.getProperty("user.home"), "Downloads")
            if (!downloadsDir.exists()) downloadsDir.mkdirs()
            val file = File(downloadsDir, fileName)

            PdfWriter.getInstance(document, FileOutputStream(file))
            document.open()

            val titleFont = Font(Font.HELVETICA, 18f, Font.BOLD)
            val subFont = Font(Font.HELVETICA, 12f, Font.NORMAL)
            val boldFont = Font(Font.HELVETICA, 12f, Font.BOLD)

            document.add(Paragraph("Business: $businessName", titleFont))
            document.add(Paragraph("Booking History", titleFont))
            document.add(Paragraph(" "))

            document.add(Paragraph("Filters:", boldFont))
            document.add(Paragraph("Staff: $staffName | Room: $roomFilter | Payment: $paymentMethod", subFont))
            document.add(Paragraph("Date Range: $dateRange", subFont))
            document.add(Paragraph(" "))

            val table = PdfPTable(6)
            table.widthPercentage = 100f

            val headers = arrayOf("No.", "Room", "Customer", "Check-in", "Payment", "Amount")
            for (header in headers) {
                table.addCell(Paragraph(header, boldFont))
            }

            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

            bookings.forEachIndexed { index, booking ->
                val dateString = try { dateFormat.format(Date(booking.checkInTime)) } catch(e: Exception) { "N/A" }
                val amountStr = Util.currencyFormat(booking.totalCost)

                table.addCell(Paragraph("${index + 1}", subFont))
                table.addCell(Paragraph(booking.roomNumber, subFont))
                table.addCell(Paragraph(booking.customerName, subFont))
                table.addCell(Paragraph(dateString, subFont))
                table.addCell(Paragraph(if (booking.isPaid) booking.paymentMethod else "Not paid", subFont))
                table.addCell(Paragraph(amountStr, subFont))
            }

            document.add(table)

            document.add(Paragraph(" "))
            val totalRevenue = bookings.sumOf { it.totalCost }
            val totalParagraph = Paragraph("Total Revenue: ${Util.currencyFormat(totalRevenue)}", boldFont)
            totalParagraph.alignment = com.lowagie.text.Element.ALIGN_RIGHT
            document.add(totalParagraph)

            document.close()

            PdfExportResult(
                message = "Saved successfully to ${file.absolutePath}",
                openTarget = file.absolutePath
            )
        } catch (e: Exception) {
            e.printStackTrace()
            PdfExportResult(message = "Error rendering PDF: ${e.message}")
        }
    }

    actual fun openPdf(openTarget: String): String? {
        return try {
            if (!Desktop.isDesktopSupported()) {
                return "Opening files is not supported on this device"
            }

            val desktop = Desktop.getDesktop()
            if (!desktop.isSupported(Desktop.Action.OPEN)) {
                return "Opening files is not supported on this device"
            }

            val file = File(openTarget)
            if (!file.exists()) {
                return "File not found"
            }

            desktop.open(file)
            null
        } catch (e: Exception) {
            "Unable to open PDF: ${e.message}"
        }
    }
}
