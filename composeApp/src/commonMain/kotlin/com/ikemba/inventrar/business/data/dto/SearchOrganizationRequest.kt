package com.ikemba.inventrar.business.data.dto

/**
 * This class represents a search criteria for organizations.
 * It contains fields to search by organizationId, organizationName, and organizationTypeId.
 */

data class SearchOrganizationRequest (

    /**
     * The unique identifier of the organization.
     */
    val organizationId : String? = null,

    /**
     * The name of the organization.
     */
    val organizationName: String? = null ,

    /**
     * The type identifier of the organization.
     */
    val organizationTypeId: String? = null,

    val searchQuery: String? = null,
val pageNumber: Int? = null,
val  numberOfRecordsPerPage: Int? = null,
)