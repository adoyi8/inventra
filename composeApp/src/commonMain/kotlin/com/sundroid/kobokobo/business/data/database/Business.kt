package com.sundroid.kobokobo.business.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Business(
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),
    val businessId: String = "",
    val name: String = "",
    val ownerId: String = "",
    var businessLogo: String? = null,
    var branchName: String? = null,
    val active: Boolean = true,
    val createdBy: String = "",
    val address: String = "",
    val features: List<String> = emptyList()
)
