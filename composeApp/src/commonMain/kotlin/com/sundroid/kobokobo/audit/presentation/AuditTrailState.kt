
package com.sundroid.kobokobo.audit.presentation

import com.sundroid.kobokobo.audit.data.domain.model.AuditTrail
import com.sundroid.kobokobo.business.data.domain.model.Staff
import com.sundroid.kobokobo.dashboard.utils.Util

data class AuditTrailState(
    val auditTrails: List<AuditTrail> = emptyList(),
    val staffList: List<Staff> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val totalRecords: Int = 0,
    val totalPages: Int = 1,
    val limit: Int = Util.DEFAULT_LIMIT,
    val page: Int = 1
)
