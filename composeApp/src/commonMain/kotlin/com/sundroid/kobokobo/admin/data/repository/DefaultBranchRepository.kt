package com.sundroid.kobokobo.admin.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sundroid.kobokobo.admin.data.domain.model.Branch
import com.sundroid.kobokobo.admin.data.domain.repository.BranchRepository
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.utils.StringUtil.BRANCHES_COLLECTION
import com.sundroid.kobokobo.utils.StringUtil.BUSINESS_COLLECTION
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class DefaultBranchRepository(
    private val firestore: FirebaseFirestore
) : BranchRepository {



    override suspend fun createBranch(branch: Branch): Result<Branch, DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(branch.businessId)
        val docRef = busRef.collection(BRANCHES_COLLECTION).document(branch.id)
        docRef.set(branch).await()
        branch
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override suspend fun updateBranch(branch: Branch): Result<Branch, DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(branch.businessId)
        val docRef = busRef.collection(BRANCHES_COLLECTION).document(branch.id)
        val updated = branch.copy(updatedAt = System.currentTimeMillis())
        docRef.set(updated).await()
        updated
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override suspend fun deleteBranch(branchId: String, businessId: String): EmptyResult<DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
        val doc = busRef.collection(BRANCHES_COLLECTION).document(branchId).get().await()
        val belongs = doc.exists() && doc.getString("businessId") == businessId
        if (!belongs) throw IllegalStateException("Branch not found or not in business")
        firestore.collection(BRANCHES_COLLECTION).document(branchId).delete().await()
    }.fold(
        onSuccess = { Result.Success(Unit) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override suspend fun getBranchById(branchId: String, businessId: String): Result<Branch?, DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
        val doc = firestore.collection(BRANCHES_COLLECTION).document(branchId).get().await()
        if (!doc.exists()) null else doc.toObject(Branch::class.java)?.takeIf { it.businessId == businessId }
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override fun getBranches(businessId: String): Flow<List<Branch>> = callbackFlow {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
        val query = busRef.collection(BRANCHES_COLLECTION)
            .orderBy("name", Query.Direction.ASCENDING)
        val listener = query.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val branches = snapshot.documents.mapNotNull { it.toObject(Branch::class.java) }
            trySend(branches)
        }
        awaitClose { listener.remove() }
    }
}












