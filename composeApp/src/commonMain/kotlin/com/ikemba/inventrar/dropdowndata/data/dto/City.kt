package com.ikemba.inventrar.dropdowndata.data.dto

import kotlinx.serialization.Serializable
// import kotlinx.serialization.SerialName // Only needed if Kotlin property name differs from JSON key

/**
 * Kotlin data class representing a geographical state.
 */
@Serializable
data class City(
    /**
     * Unique identifier for the state.
     */

    val cityId: String,
    val cityName: String,

    val stateId: String?,

    /**
     * The ID of the country to which this state belongs.
     */
    val countryId: String?,

    /**
     * The name of the state.
     */
    val stateName: String?,

    /**
     * Indicates whether the state is voided or not.
     */
    val voided: Boolean?,

    /**
     * The ID of the user who created the state.
     */
    val createdBy: String?,

    /**
     * The ID of the user who last updated the state.
     */
    val updatedBy: String?,

    /**
     * The ID of the user who voided the state.
     */
    val voidedBy: String?,

    /**
     * The timestamp of when the state was created.
     * Represented as a String for multiplatform compatibility and simpler JSON handling.
     */
    val dateCreated: String?,

    /**
     * The timestamp of when the state was last updated.
     * Represented as a String for multiplatform compatibility and simpler JSON handling.
     */
    val dateUpdated: String?,

    /**
     * The timestamp of when the state was voided.
     * Represented as a String for multiplatform compatibility and simpler JSON handling.
     */
    val dateVoided: String?
)