package com.ikemba.inventrar.dropdowndata.data.dto

data class CityResponse (
    /**
     * The response code indicating the status of the operation.
     * It should be a positive integer.
     */
    val responseCode: Int,
    val responseMessage: String,
    val cities: List<City>
)