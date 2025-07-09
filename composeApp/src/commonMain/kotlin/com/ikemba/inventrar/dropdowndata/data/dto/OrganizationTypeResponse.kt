package com.ikemba.inventrar.dropdowndata.data.dto

import kotlinx.serialization.Serializable


@Serializable
data class OrganizationTypeResponse (
    /**
     * The response code indicating the status of the operation.
     * It should be a positive integer.
     */
    val responseCode: Int,
   val responseMessage: String,
    val organizationTypes: List<OrganizationType>? = null
)