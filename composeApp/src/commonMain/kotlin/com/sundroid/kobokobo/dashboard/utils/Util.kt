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
    val BASE_URL = "http://api.inventrar.com/v1"
    val NGROK_URL = "https://70848854e5d8.ngrok-free.app"
    val REQUEST_TOKEN_ID = "680073319724-vbfqsn9e32vqvb7pdfhhmqq234fieg51.apps.googleusercontent.com"  //console
    val USER_SERVICE_URL = "$NGROK_URL/api/v1/user_service/public"
    val SETTINGS_SERVICE_URL = "$NGROK_URL/api/v1/settings_service/public"
    val INVENTORY_SERVICE_URL = "$NGROK_URL/api/v1/inventory_service/public"
    val ORGANIZATION_SERVICE_URL = "$NGROK_URL/api/v1/organization_service/public"
    val AUTHORIZATION_TOKEN = "OGFiMjU2ZTJlOGI1NDMyNjg0NWRmMjJhOGIwOTE1OGE="
    val APP_NAME: String = "Skobo"
    //val BASE_URL = "http://api.ikembatech.com.au/v1"
    var accessToken: String = ""
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