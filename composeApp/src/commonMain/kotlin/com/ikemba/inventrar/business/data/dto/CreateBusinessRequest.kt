package com.ikemba.inventrar.business.data.dto



import kotlinx.serialization.Serializable
import org.jsoup.select.Evaluator.Id
import java.time.LocalDateTime



/**
 * This class represents an organization within the Visitavia system. It is an entity that is persisted in the database.
 *
 * @author Tabnine team
 */


@Serializable
data class CreateBusinessRequest(

  val organizationName: String? = null,

    /**
     * The type of organization (e.g., airline, travel agency, etc.).
     */
    val organizationTypeId: String? = null,

    /**
     * A brief description of the organization.
     */
    val description: String? = null,

    /**
     * The state  id or province where the organization is located.
     */
    val stateId: String? = null,


    /**
     * The country id where the organization is located.
     */
     val countryId: String? = null,


    /**
     * The city id where the organization is located.
     */
     val cityId: String? = null,



    /**
     * The physical address of the organization.
     */
     val address: String? = null,


    /**
     * The unique identifier of the primary administrator for the organization.
     */
     val primaryAdminId: String? = null,


    /**
     * The unique identifier of the user who created the organization.
     */
     val createdBy: String? = null,

)
