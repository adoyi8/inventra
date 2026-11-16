package com.sundroid.kobokobo.audit.data.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class AuditTrail(
    val id: String = "",
    val businessId: String = "",
    val staffId: String = "",
    val actionType: ActionType = ActionType.UNKNOWN,
    val entityType: EntityType = EntityType.UNKNOWN,
    val entityId: String = "",
    val auditTrailMessage: String = "",
    @ServerTimestamp val createdAt: Date? = null,
    val productId: String? = null,
    val transactionId: String? = null,
    val categoryId: String? = null,
    val locationId: String? = null,
    val newValues: Map<String, Any?>? = null,

    )



enum class ActionType {
    UNKNOWN,
    CREATE,
    UPDATE,
    DELETE,
    VIEW,
    MOVE,
    TRANSFER,
    ADJUST,
    SALE,
    RETURN,
    DAMAGE
}

enum class EntityType {
    UNKNOWN,
    PRODUCT,
    CATEGORY,
    LOCATION,
    STOCK_LEVEL,
    STOCK_MOVEMENT,
    STAFF,
    DEPARTMENT,
    BRANCH,
    BUSINESS,
    TRANSACTION
}









