package com.ikemba.inventrar.dashboard.data.database

import androidx.room.TypeConverter
import com.ikemba.inventrar.cart.data.dto.PostSalesItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object StringListTypeConverter {

    @TypeConverter
    fun fromString(value: String): List<PostSalesItem> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromList(list: List<PostSalesItem>): String {
        return Json.encodeToString(list)
    }
}