package com.sundroid.kobokobo.audit.data.domain.model

import com.sundroid.kobokobo.dashboard.utils.Util

data class AuditTrailDto(
    val auditTrails: List<AuditTrail> = emptyList(),
    val totalRecords: Int = 0,
    val totalPages: Int = 1,
    val limit: Int = Util.DEFAULT_LIMIT,
    val page: Int = 1
)