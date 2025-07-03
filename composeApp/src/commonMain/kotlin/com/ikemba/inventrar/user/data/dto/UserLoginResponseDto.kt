package com.ikemba.inventrar.user.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable




/**
 * Base response class providing common fields like response code and message.
 */
@Serializable
data class Response(
    /**
     * The response code indicating the status of the operation.
     * It should be a positive integer.
     */
    val responseCode: Int,

    /**
     * The response message providing additional information about the operation's outcome.
     * It can be null if no specific message is required.
     */
    val responseMessage: String? // Made nullable as per your Java class description
)

/**
 * This class represents the details of a user login.
 * It contains various attributes related to the user's profile and authentication status.
 */
@Serializable
data class UserLoginDetails(
    /**
     * The unique identifier of the employee.
     */
    val employeeId: String?,

    /**
     * The unique identifier of the country associated with the employee.
     */
    val countryId: String?,

    /**
     * The first name of the employee.
     */
    val firstName: String?,

    /**
     * The email address of the employee.
     */
    val emailAddress: String?,

    /**
     * The unique identifier of the organization associated with the employee.
     */
    val organizationId: String?,

    /**
     * The unique identifier of the branch associated with the employee.
     */
    val branchId: String?,

    /**
     * The phone country code of the employee.
     */
    val phoneCountryCode: String?,

    /**
     * The phone number of the employee.
     */
    val phoneNumber: String?,

    /**
     * Indicates whether the employee has accepted the terms and conditions.
     */
    val isTermAndCondition: Boolean?,

    /**
     * Indicates whether the employee has created password.
     */
    val isPasswordCreated: Boolean?,

    /**
     * The last name of the employee.
     */
    val lastName: String?,

    /**
     * The access token for the authenticated user.
     */
    val token: String?,

    /**
     * The refresh token for the authenticated user.
     */
    val refreshToken: String?,

    /**
     * Indicates whether the employee's email address has been verified.
     */
    val isEmailVerified: Boolean?,

    /**
     * The date and time when the user's account was created.
     *
     * IMPORTANT: For `LocalDateTime`, you have two main options with Kotlinx Serialization:
     * 1. Use a custom serializer: Recommended if you need full `LocalDateTime` objects.
     * 2. Receive as `String` and convert: Simpler if you just need the string representation.
     *
     * I've set it to `String?` here as it's often easier for dates/times from JSON if
     * you just need to display them or convert them later. If you truly need `LocalDateTime`
     * directly deserialized, you'll need a custom serializer.
     *
     * Alternatively, consider `kotlinx.datetime.LocalDateTime` for a multiplatform solution
     * if your project extends beyond Android/JVM and you add the `kotlinx-datetime` dependency.
     */
    val dateCreated: String?, // Changed to String? for simpler JSON handling

    /**
     * Indicates whether the user's account is activated.
     */
    val isAccountActivated: Boolean?,

    /**
     * The expiration time for the access token in ISO 8601 format.
     */
    val setExpirationTime: String?,

    /**
     * The roleId of the user, details about the role can be gotten from settings service
     */
    val roleId: String?
)

/**
 * The main response class for user login, containing the base response fields
 * and the employee details.
 */
@Serializable
data class UserLoginResponseDto(
    // Explicitly include fields from Response to flat-map them in JSON
    @SerialName("responseCode")
    val responseCode: Int,

    @SerialName("responseMessage")
    val responseMessage: String?,

    /**
     * The employee details associated with the user login.
     */
    val employee: UserLoginDetails? // Made nullable as the employee object might not always be present on error responses
)