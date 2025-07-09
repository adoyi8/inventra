package com.ikemba.inventrar.dashboard.utils

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


object Util {


    val PAYMENT_METHOD_TRANSFER: String = "transfer"
    val PAYMENT_METHOD_CASH: String = "cash"
    val PAYMENT_METHOD_CARD: String = "card"
    var CURRENCY_CODE: String = ""
    val EXPIRED_TOKEN_MESSAGE = "Expired"
    val BASE_URL = "http://api.inventrar.com/v1"
    val NGROK_URL = "https://70848854e5d8.ngrok-free.app"
    val REQUEST_TOKEN_ID = "926103425935-1up36j1r082lqvfbv196e97im3mct9jb.apps.googleusercontent.com"  //console
    val USER_SERVICE_URL = "$NGROK_URL/api/v1/user_service/public"
    val SETTINGS_SERVICE_URL = "$NGROK_URL/api/v1/settings_service/public"
    val INVENTORY_SERVICE_URL = "$NGROK_URL/api/v1/inventory_service/public"
    val ORGANIZATION_SERVICE_URL = "$NGROK_URL/api/v1/organization_service/public"
    val AUTHORIZATION_TOKEN = "OGFiMjU2ZTJlOGI1NDMyNjg0NWRmMjJhOGIwOTE1OGE="
    val APP_NAME = "visitavia-mobile"
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

    fun formatDate(date: LocalDate): String {
        val day = date.dayOfMonth
        val monthYear = date.format(DateTimeFormatter.ofPattern("MMMM, yyyy", Locale.ENGLISH))

        val suffix = when {
            day in 11..13 -> "th"
            day % 10 == 1 -> "st"
            day % 10 == 2 -> "nd"
            day % 10 == 3 -> "rd"
            else -> "th"
        }

        return "$day$suffix $monthYear"

    }
    fun formatTime(time: LocalTime): String {
        val formatter = DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH)
        return time.format(formatter).lowercase() // Converts AM/PM to lowercase
    }
}