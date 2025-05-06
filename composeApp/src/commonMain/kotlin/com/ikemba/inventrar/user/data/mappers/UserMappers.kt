package com.ikemba.inventrar.user.data.mappers


import com.ikemba.inventrar.user.data.database.UserEntity
import com.ikemba.inventrar.user.data.dto.UserLoginResponseDto
import com.ikemba.inventrar.user.domain.User


fun UserLoginResponseDto.toUser(): User {
    return User(
        firstname= userResponseData?.account?.firstname,
    middlename = userResponseData?.account?.middlename,
    lastname = userResponseData?.account?.lastname,
     email  = userResponseData?.account?.email,
     username  = userResponseData?.account?.username,
     roleId  = userResponseData?.account?.roleId,
     roleName  = userResponseData?.account?.roleName,
     staffNo  = userResponseData?.account?.staffNo,
     officeId = userResponseData?.account?.officeId,
    countryId = userResponseData?.account?.countryId,
     stateId = userResponseData?.account?.stateId,
     cityId = userResponseData?.account?.cityId,
     merchantId = userResponseData?.account?.merchantId,
     businessName = userResponseData?.account?.businessName,
     businessEmail = userResponseData?.account?.businessEmail,
     contactPhone = userResponseData?.account?.contactPhone,
     currencyCode = userResponseData?.account?.currencyCode,
     currencyEntity = userResponseData?.account?.currencyEntity,
     userTimezone = userResponseData?.account?.userTimezone,
     subscriptionId = userResponseData?.account?.subscriptionId,
     businessLogo = userResponseData?.account?.businessLogo,
     branchName = userResponseData?.account?.branchName,
     defaultTax = userResponseData?.account?.defaultTax,
     accessToken = userResponseData?.auth?.accessToken
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
        accessToken = accessToken
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
        accessToken = accessToken
    )
}