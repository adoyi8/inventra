package com.ikemba.inventrar.dropdowndata.data.dto

import kotlinx.serialization.Serializable
import org.jsoup.select.Evaluator.Id
import java.time.LocalDateTime




@Serializable
data class Country (
    /**
     * Unique identifier for the country.
     */

     val countryId: String? = null,

    /**
     * Name of the country.
     */
    val countryName: String? = null,

    /**
     * Phone code associated with the country.
     */
    val countryPhoneCode: String? = null,

    /**
     * ISO code for the country.
     */
    val isoCode: String? = null,

    /**
     * Indicates whether the country is voided or not.
     */
    val voided: Boolean? = null,

    /**
     * Identifier of the user who created the country.
     */
    val createdBy: String? = null,

    /**
     * Identifier of the user who last updated the country.
     */
    val updatedBy: String? = null,

    /**
     * Identifier of the user who voided the country.
     */
    val voidedBy: String? = null,

)