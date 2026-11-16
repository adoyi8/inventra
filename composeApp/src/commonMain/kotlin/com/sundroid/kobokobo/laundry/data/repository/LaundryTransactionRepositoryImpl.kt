package com.sundroid.kobokobo.laundry.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.laundry.data.domain.model.LaundryTransaction
import com.sundroid.kobokobo.laundry.data.domain.repository.LaundryTransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose

class LaundryTransactionRepositoryImpl(
    private val firestore: FirebaseFirestore
) : LaundryTransactionRepository {

    private val collectionName = "laundry_transactions"
    private val BUSINESS_COLLECTION = "businesses"

    override suspend fun postLaundryTransaction(businessId: String, transaction: LaundryTransaction): Result<LaundryTransaction, DataError> {
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

    override fun getLaundryTransactions(businessId: String): Flow<List<LaundryTransaction>> = callbackFlow {
        val query = firestore.collection(BUSINESS_COLLECTION).document(businessId)
            .collection(collectionName)
        val listener = query.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val transactions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(LaundryTransaction::class.java)?.copy(id = doc.id)
            }.sortedByDescending { it.timestamp }
            trySend(transactions)
        }
        awaitClose { listener.remove() }
    }
}
