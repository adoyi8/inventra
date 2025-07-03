package com.ikemba.inventrar.dropdowndata.data.dto

import org.jsoup.select.Evaluator.Id
import java.time.LocalDateTime



data class Country (
    /**
     * Unique identifier for the country.
     */

    private val countryId: String? = null,

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

    /**
     * Date and time when the country was created.
     */
    val dateCreated: LocalDateTime? = null,

    /**
     * Date and time when the country was last updated.
     */
    val dateUpdated: LocalDateTime? = null,

    /**
     * Date and time when the country was voided.
     */
    val dateVoided: LocalDateTime? = null
)