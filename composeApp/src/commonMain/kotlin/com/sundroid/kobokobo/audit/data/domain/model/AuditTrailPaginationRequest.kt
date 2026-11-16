package com.sundroid.kobokobo.audit.data.domain.model

import com.sundroid.kobokobo.dashboard.utils.Util

data class AuditTrailPaginationRequest(
    var start_date: String? = null,
    var end_date: String? = null,
    var staff_user_id: String? = null,
    var action_type: ActionType? = null,
    var entity_type: EntityType? = null,
    var filter_by: String? = null, // For quick filters like "daily", "weekly"
    var page: Long = 1,
    var limit: Int = Util.DEFAULT_LIMIT,
    var productId: String? = null
)