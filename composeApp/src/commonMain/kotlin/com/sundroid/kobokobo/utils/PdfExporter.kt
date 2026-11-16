package com.sundroid.kobokobo.utils

import androidx.compose.runtime.Composable
import com.sundroid.kobokobo.hotel.domain.model.Booking
import com.sundroid.kobokobo.transactionHistory.domain.OrderModel

data class PdfExportResult(
    val message: String,
    val openTarget: String? = null
)

@Composable
expect fun rememberPdfExporter(): PdfExporter

expect class PdfExporter {
    fun exportTransactionPdf(
        businessName: String,
        staffName: String,
        paymentMethod: String,
        dateRange: String,
        pageStr: String,
        transactions: List<OrderModel>
    ): PdfExportResult

    fun openPdf(openTarget: String): String?

    fun exportBookingHistoryPdf(
        businessName: String,
        dateRange: String,
        staffName: String,
        roomFilter: String,
        paymentMethod: String,
        bookings: List<Booking>
    ): PdfExportResult
}
