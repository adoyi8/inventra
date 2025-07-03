package com.ikemba.inventrar.dropdowndata.data.dto

data class CountryResponse (
    /**
     * The response code indicating the status of the operation.
     * It should be a positive integer.
     */
    val responseCode: Int,
val responseMessage: String,
    val countries: List<Country>
)