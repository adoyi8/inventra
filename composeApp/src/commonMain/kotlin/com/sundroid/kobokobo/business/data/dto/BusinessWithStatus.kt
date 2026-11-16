package com.sundroid.kobokobo.business.data.dto

import com.sundroid.kobokobo.business.data.database.Business

data class BusinessWithStatus(
    val business: Business,
    val isStaffActive: Boolean,
    val isBusinessActive: Boolean
) {
    // A helper to check if the entry is fully active
    val isActive: Boolean
        get() = isStaffActive && isBusinessActive
}