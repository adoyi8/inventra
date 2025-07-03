package com.ikemba.inventrar.business.data.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
@Serializable
data class OrganizationResponse(
    // Explicitly include fields from Response, as Kotlinx Serialization
    // doesn't "inherit" JSON structure from parent classes in the same way Lombok does.
    @SerialName("responseCode")
    val responseCode: Int,

    @SerialName("responseMessage")
    val responseMessage: String?,

    /**
     * A list of [SearchOrganizationResult] objects representing the search results.
     */
    val organizations: List<SearchOrganizationResult>? // Made nullable in case the list itself can be null in response
)
@Serializable
data class SearchOrganizationResult(
    /**
     * The unique identifier of the organization.
     */
    val organizationId: String,

    /**
     * The name of the organization.
     */
    val organizationName: String,

    /**
     * The unique identifier of the organization type.
     */
    val organizationTypeId: String?,

    /**
     * The name of the organization type.
     */
    val organizationTypeName: String?,

    /**
     * A brief description of the organization.
     */
    val description: String?,

    /**
     * The unique identifier of the state where the organization is located.
     */
    val stateId: String?,

    /**
     * The name of the state where the organization is located.
     */
    val stateName: String?,

    /**
     * The unique identifier of the country where the organization is located.
     */
    val countryId: String?,

    /**
     * The name of the country where the organization is located.
     */
    val countryName: String?,

    /**
     * The unique identifier of the city where the organization is located.
     */
    val cityId: String?,

    /**
     * The name of the city where the organization is located.
     */
    val cityName: String?,

    /**
     * The physical address of the organization.
     */
    val address: String?,

    /**
     * The email address of the organization.
     */
    val emailAddress: String?,

    /**
     * The phone country code of the organization.
     */
    val phoneCountryCode: String?,

    /**
     * The phone number of the organization.
     */
    val phoneNumber: String?,

    /**
     * The unique identifier of the primary administrator of the organization.
     */
    val primaryAdminId: String?,

    /**
     * The full name of the primary administrator of the organization.
     */
    val primaryAdminFullName: String?,

    /**
     * The URL of the logo of the organization.
     */
    val logo: String?,

    /**
     * The number of branches associated with the organization.
     * Default value is 0.
     */
    val numberOfBranches: Int = 0, // Keeping default value as per Java, assuming it's always serialized

    /**
     * The headquarters of the organization.
     */
    val headQuarters: String?
)