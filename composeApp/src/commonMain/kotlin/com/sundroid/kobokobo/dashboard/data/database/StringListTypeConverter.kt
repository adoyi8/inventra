package com.sundroid.kobokobo.dashboard.data.database

import androidx.room.TypeConverter
import com.sundroid.kobokobo.cart.domain.CartItem
import kotlinx.serialization.json.Json

object StringListTypeConverter {

//    @TypeConverter
//    fun fromString(value: String): List<PostSalesItem> {
//        return Json.decodeFromString(value)
//    }

    @TypeConverter
    fun fromString(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split(",").map { it.trim() }
    }

    @TypeConverter
    fun fromList(list: List<String>?): String {
        return list?.joinToString(",") ?: ""
    }
}


class CartItemTypeConverter {
    // We'll use a default Json instance. Configure if you need special settings.
    private val json = Json { ignoreUnknownKeys = true } // Example config

    @TypeConverter
    fun fromCartItemList(list: List<CartItem>?): String? {
        if (list == null) return null
        return json.encodeToString(list)
    }

    @TypeConverter
    fun toCartItemList(jsonString: String?): List<CartItem>? { // Renamed param to avoid confusion
        if (jsonString == null) return null
        return json.decodeFromString(jsonString)
    }
}