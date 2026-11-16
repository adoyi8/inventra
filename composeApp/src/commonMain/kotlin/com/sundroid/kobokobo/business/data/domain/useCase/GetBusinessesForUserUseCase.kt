package com.sundroid.kobokobo.business.data.domain.useCase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.sundroid.kobokobo.business.data.database.Business
import com.sundroid.kobokobo.business.data.domain.model.Staff
import com.sundroid.kobokobo.business.data.dto.BusinessWithStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map


class GetBusinessesForUserUseCase(
    private val firestore: FirebaseFirestore
) {
    operator fun invoke(userId: String): Flow<List<BusinessWithStatus>> {
        // 1. Listen to 'staff' collection
        return firestore.collection("staff")
            .whereEqualTo("userId", userId)
            .snapshots()
            .flatMapLatest { staffSnapshot ->
                val allStaffEntries = staffSnapshot.toObjects(Staff::class.java)

                // Create a map of businessId -> Staff entry for easy lookup later
                val staffMap = allStaffEntries.associateBy { it.businessId }
                val allBusinessIds = staffMap.keys.toList()

                if (allBusinessIds.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    // 2. Listen to 'businesses' collection for ALL found IDs (active or not)
                    firestore.collection("businesses")
                        .whereIn("businessId", allBusinessIds.take(10)) // Handle 10-item limit
                        .snapshots()
                        .map { businessSnapshot ->
                            val businesses = businessSnapshot.toObjects(Business::class.java)

                            // 3. Combine Business data with Staff status
                            businesses.mapNotNull { business ->
                                val staffEntry = staffMap[business.businessId]
                                // Should not happen if data integrity is maintained, but good to be safe
                                if (staffEntry == null) return@mapNotNull null

                                BusinessWithStatus(
                                    business = business,
                                    isStaffActive = staffEntry.active,
                                    isBusinessActive = business.active
                                )
                            }
                        }
                }
            }
    }
}