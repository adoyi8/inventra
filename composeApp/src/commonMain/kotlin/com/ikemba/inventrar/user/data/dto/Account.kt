package com.ikemba.inventrar.user.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Account(

    @SerialName("firstname") var firstname: String? = null,
    @SerialName("middlename") var middlename: String? = null,
    @SerialName("lastname") var lastname: String? = null,
    @SerialName("email") var email: String? = null,
    @SerialName("username") var username: String? = null,
    @SerialName("role_id") var roleId: String? = null,
    @SerialName("role_name") var roleName: String? = null,
    @SerialName("staff_no") var staffNo: String? = null,
    @SerialName("office_id") var officeId: String? = null,
    @SerialName("country_id") var countryId: String? = null,
    @SerialName("state_id") var stateId: String? = null,
    @SerialName("city_id") var cityId: String? = null,
    @SerialName("merchant_id") var merchantId: String? = null,
    @SerialName("business_name") var businessName: String? = null,
    @SerialName("business_email") var businessEmail: String? = null,
    @SerialName("contact_phone") var contactPhone: String? = null,
    @SerialName("currency_code") var currencyCode: String? = null,
    @SerialName("currency_entity") var currencyEntity: String? = null,
    @SerialName("user_timezone") var userTimezone: String? = null,
    @SerialName("subscription_id") var subscriptionId: String? = null,
    @SerialName("business_logo") var businessLogo: String? = null,
    @SerialName("branch_name") var branchName: String? = null,
    @SerialName("default_tax") var defaultTax: String? = null

)