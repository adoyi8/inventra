package com.sundroid.kobokobo.dashboard.utils

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date


object Util {


    val DEFAULT_LIMIT: Int = 100
    val DEFAULT_ADMIN_EMAIL: String = "seesayapp@gmail.com"
    val PAYMENT_METHOD_TRANSFER: String = "transfer"
    val PAYMENT_METHOD_CASH: String = "cash"
    val PAYMENT_METHOD_CARD: String = "card"
    var CURRENCY_CODE: String = ""
    val EXPIRED_TOKEN_MESSAGE = "Expired"
    val REQUEST_TOKEN_ID = "680073319724-vbfqsn9e32vqvb7pdfhhmqq234fieg51.apps.googleusercontent.com"  //console
    val APP_NAME: String = "Skobo"
    fun generateTransactionReference(): String {
        // Get current timestamp
        val timestamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())

        // Combine timestamp and UUID part to form a unique transaction reference
        return "$timestamp"
    }

    fun decFormat(value: Double): String {
        val dec = DecimalFormat("#,###,###.##")
        return dec.format(value)
    }

    fun currencyFormat(value: Double): String {
        return "${CURRENCY_CODE}${decFormat(value)}"
    }

    fun formatDate(millis: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        return formatter.format(Date(millis))
    }
}