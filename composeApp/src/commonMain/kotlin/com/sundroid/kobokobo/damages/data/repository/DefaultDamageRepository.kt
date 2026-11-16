package com.sundroid.kobokobo.damages.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sundroid.kobokobo.audit.data.domain.model.ActionType
import com.sundroid.kobokobo.audit.data.domain.model.AuditTrail
import com.sundroid.kobokobo.audit.data.domain.model.EntityType
import com.sundroid.kobokobo.audit.data.domain.repository.AuditTrailRepository
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.damages.data.domain.Damage
import com.sundroid.kobokobo.damages.data.domain.DamageRepository
import com.sundroid.kobokobo.dashboard.utils.Util
import com.sundroid.kobokobo.utils.StringUtil.PRODUCTS_COLLECTION
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class DefaultDamageRepository(
    private val firestore: FirebaseFirestore,
    private val auditTrailRepository: AuditTrailRepository
) : DamageRepository {

    private val BUSINESS_COLLECTION = "businesses"

    override suspend fun postDamage(damage: Damage): Result<Unit, DataError.Remote> {
        return try {
            val ref = Util.generateTransactionReference() // Generate a unique reference
            val busRef = firestore.collection(BUSINESS_COLLECTION).document(damage.businessId)

            // Create a PostSalesRequest-like structure for the damage record
            // Note: We are saving it in the 'sales' collection as requested.
            val data = hashMapOf(
                "reference" to ref,
                "type" to "damage", // <--- IMPORTANT: Mark as damage
                "cart" to listOf(
                    mapOf(
                        "product_id" to damage.productId,
                        "product_name" to damage.productName,
                        "qty" to damage.quantity.toString(),
                        "costPrice" to damage.costPrice,
                        // Profit for a damage record is negative of its cost
                        "profit" to -damage.totalCost,
                        "subtotal" to 0.0, // No selling price for damages
                        "vat_rate" to 0.0,
                        "vat_amount" to 0.0,
                        "discount" to 0.0
                    )
                ),
                "total" to 0.0,
                "total_vat" to 0.0,
                "grandTotal" to 0.0,
                // Total profit for the transaction is negative of total cost
                "total_profit" to -damage.totalCost,
                "payment_method" to "N/A",
                "is_held" to false,
                "reason" to damage.reason,
                "createdAt" to FieldValue.serverTimestamp(),
                "createdBy" to damage.staffId,
                "createdByName" to damage.staffName // Useful for display
            )

            busRef.collection("sales").document(ref).set(data).await()

            // Update inventory (stock level)
            val productRef = firestore.collection(BUSINESS_COLLECTION)
                .document(damage.businessId)
                .collection(PRODUCTS_COLLECTION)
                .document(damage.productId)

            productRef.update("stock", FieldValue.increment(-damage.quantity.toDouble())).await()

            // Log Audit Trail
            auditTrailRepository.createAuditTrail(
                AuditTrail(
                    businessId = damage.businessId,
                    staffId = damage.staffId,
                    actionType = ActionType.DAMAGE,
                    entityType = EntityType.PRODUCT,
                    entityId = damage.productId,
                    auditTrailMessage = "${damage.staffName} damaged ${damage.quantity} of ${damage.productName}. Reason: ${damage.reason}",
                    productId = damage.productId,
                    transactionId = ref,
                    newValues = mapOf("quantity" to damage.quantity, "reason" to damage.reason)
                )
            )

            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
    override fun getDamages(businessId: String): Flow<List<Damage>> = callbackFlow {
        val query = firestore.collection(BUSINESS_COLLECTION).document(businessId)
            .collection("sales")
            .whereEqualTo("type", "damage")
            .orderBy("createdAt", Query.Direction.DESCENDING)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val damages = snapshot?.documents?.mapNotNull { doc ->
                try {
                    val cart = doc.get("cart") as? List<Map<String, Any>> ?: emptyList()
                    val firstItem = cart.firstOrNull() ?: return@mapNotNull null

                    Damage(
                        id = doc.id,
                        businessId = businessId,
                        productId = firstItem["product_id"] as? String ?: "",
                        productName = firstItem["product_name"] as? String ?: "",
                        quantity = (firstItem["qty"] as? String)?.toIntOrNull() ?: 0,
                        costPrice = (firstItem["costPrice"] as? Number)?.toDouble() ?: 0.0,
                        staffId = doc.getString("createdBy") ?: "",
                        staffName = doc.getString("createdByName") ?: "",
                        reason = doc.getString("reason") ?: "",
                        createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            } ?: emptyList()

            trySend(damages)
        }
        awaitClose { listener.remove() }
    }
}