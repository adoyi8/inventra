package com.ikemba.inventrar.dropdowndata.data.dto

import kotlinx.serialization.Serializable
// import kotlinx.serialization.SerialName // Only needed if Kotlin property name differs from JSON key

/**
 * Kotlin data class representing an Organization Type.
 * Designed for serialization for data transfer.
 */
@Serializable
data class OrganizationType(
    /**
     * Unique identifier for the organization type.
     */
    val organizationTypeId: String? = null,

    /**
     * The type of organization.
     */
    val organizationType: String? = null,

    /**
     * The date and time when the organization type was created.
     * Represented as a String for multiplatform compatibility and simpler JSON handling.
     */
    val dateCreated: String? = null,

    /**
     * The date and time when the organization type was last updated.
     * Represented as a String for multiplatform compatibility and simpler JSON handling.
     */
    val dateUpdated: String? = null,

    /**
     * The date and time when the organization type was voided.
     * Represented as a String for multiplatform compatibility and simpler JSON handling.
     * This field is null if the organization type is not voided.
     */
    val dateVoided: String? = null,

    /**
     * The user who created the organization type.
     */
    val createdBy: String? = null,

    /**
     * The user who last updated the organization type.
     */
    val updatedBy: String? = null,

    /**
     * The user who voided the organization type.
     * This field is null if the organization type is not voided.
     */
    val voidedBy: String? = null,

    /**
     * Indicates whether the organization type is voided or not.
     * True if voided, false otherwise.
     */
    val voided: Boolean? = false// Made nullable for flexibility in JSON responses
)