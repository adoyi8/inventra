package com.sundroid.kobokobo.services.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.services.data.domain.model.ServicesTransaction
import com.sundroid.kobokobo.services.data.domain.repository.ServicesTransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose

class ServicesTransactionRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ServicesTransactionRepository {

    private val collectionName = "services_transactions"
    private val BUSINESS_COLLECTION = "businesses"

    override suspend fun postServicesTransaction(businessId: String, transaction: ServicesTransaction): Result<ServicesTransaction, DataError> {
        return try {
            firestore.collection(BUSINESS_COLLECTION).document(businessId)
                .collection(collectionName)
                .document(transaction.id)
                .set(transaction)
                .await()
            Result.Success(transaction)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    override fun getServicesTransactions(businessId: String): Flow<List<ServicesTransaction>> = callbackFlow {
        val query = firestore.collection(BUSINESS_COLLECTION).document(businessId)
            .collection(collectionName)
        val listener = query.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val transactions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(ServicesTransaction::class.java)?.copy(id = doc.id)
            }.sortedByDescending { it.timestamp }
            trySend(transactions)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun markTransactionPaid(
        businessId: String,
        transactionId: String,
        paymentMethod: String,
        processedBy: String
    ): Result<Unit, DataError> = try {
        firestore.collection(BUSINESS_COLLECTION).document(businessId)
            .collection(collectionName).document(transactionId)
            .update(
                mapOf(
                    "paymentMethod" to paymentMethod,
                    "isPaid" to true,
                    "paymentProcessedBy" to processedBy
                )
            ).await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(DataError.Remote.UNKNOWN)
    }
}
