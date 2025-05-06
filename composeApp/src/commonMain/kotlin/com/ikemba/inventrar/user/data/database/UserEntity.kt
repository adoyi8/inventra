package com.ikemba.inventrar.user.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),
    var firstname: String? = null,
    var middlename: String? = null,
    var lastname: String? = null,
    var email: String? = null,
    var username: String? = null,
    var roleId: String? = null,
    var roleName: String? = null,
    var staffNo: String? = null,
    var officeId: String? = null,
    var countryId: String? = null,
    var stateId: String? = null,
    var cityId: String? = null,
    var merchantId: String? = null,
    var businessName: String? = null,
    var businessEmail: String? = null,
    var contactPhone: String? = null,
    var currencyCode: String? = null,
    var currencyEntity: String? = null,
    var userTimezone: String? = null,
    var subscriptionId: String? = null,
    var businessLogo: String? = null,
    var branchName: String? = null,
    var defaultTax: String? = null,
    var accessToken: String? = null
)
