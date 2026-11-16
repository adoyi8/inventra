package com.sundroid.kobokobo.user.data.mappers


import com.google.firebase.auth.FirebaseUser
import com.sundroid.kobokobo.user.data.database.UserEntity
import com.sundroid.kobokobo.user.data.dto.UserLoginResponseDto
import com.sundroid.kobokobo.user.domain.User


fun UserLoginResponseDto.toUser(): User {

    return User(
        firstname= employee?.firstName,
    lastname = employee?.lastName,
     email  = employee?.emailAddress,
     roleId  = employee?.roleId,
    countryId = employee?.countryId,
     cityId = employee?.countryId,
        userId = employee?.employeeId
    )
}
fun FirebaseUser.toUser(): User {

    return User(
        firstname= displayName,
        email  = email,
        userId = uid
    )
}



fun User.toUserEntity(): UserEntity {
    return UserEntity(
        id = id,
        firstname= firstname,
        middlename = middlename,
        lastname = lastname,
        email  = email,
        username  = username,
        roleId  = roleId,
        roleName  = roleName,
        staffNo  = staffNo,
        officeId = officeId,
        countryId = countryId,
        stateId = stateId,
        cityId = cityId,
        merchantId = merchantId,
        businessName = businessName,
        businessEmail = businessEmail,
        contactPhone = contactPhone,
        currencyCode = currencyCode,
        currencyEntity = currencyEntity,
        userTimezone = userTimezone,
        subscriptionId = subscriptionId,
        businessLogo = businessLogo,
        branchName = branchName,
        defaultTax = defaultTax,
        accessToken = accessToken,
        userId = userId,
        organizationId = businessId,
        permissions = permissions,
        businessFeatures = businessFeatures
    )
}

fun UserEntity.toUser(): User {
    return User(
        id = id,
        firstname= firstname,
        middlename = middlename,
        lastname = lastname,
        email  = email,
        username  = username,
        roleId  = roleId,
        roleName  = roleName,
        staffNo  = staffNo,
        officeId = officeId,
        countryId = countryId,
        stateId = stateId,
        cityId = cityId,
        merchantId = merchantId,
        businessName = businessName,
        businessEmail = businessEmail,
        contactPhone = contactPhone,
        currencyCode = currencyCode,
        currencyEntity = currencyEntity,
        userTimezone = userTimezone,
        subscriptionId = subscriptionId,
        businessLogo = businessLogo,
        branchName = branchName,
        defaultTax = defaultTax,
        accessToken = accessToken,
        userId = userId,
        businessId = organizationId,
        permissions = permissions,
        businessFeatures = businessFeatures
    )
}