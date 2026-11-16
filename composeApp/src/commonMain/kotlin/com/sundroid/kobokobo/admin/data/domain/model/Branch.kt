package com.sundroid.kobokobo.admin.data.domain.model

import java.util.UUID

data class Branch(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val businessId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String? = null,
    val updatedBy: String? = null
)




