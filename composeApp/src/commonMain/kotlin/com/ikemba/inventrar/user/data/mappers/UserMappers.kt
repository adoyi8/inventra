package com.ikemba.inventrar.user.data.mappers


import com.ikemba.inventrar.user.data.database.UserEntity
import com.ikemba.inventrar.user.data.dto.UserLoginResponseDto
import com.ikemba.inventrar.user.domain.User


fun UserLoginResponseDto.toUser(): User {
    println("sarank 1 "+ employee?.employeeId)
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

fun User.toUserEntity(): UserEntity {
    println("sarank 2 "+ userId)
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
        organizationId = organizationId,
        organizationName = organizationName
    )
}

fun UserEntity.toUser(): User {
    println("sarank 3"+ userId)
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
        organizationId = organizationId,
        organizationName = organizationName
    )
}