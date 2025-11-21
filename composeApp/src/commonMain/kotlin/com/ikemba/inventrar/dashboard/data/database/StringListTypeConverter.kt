package com.ikemba.inventrar.dashboard.data.database

import androidx.room.TypeConverter

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