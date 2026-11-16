package com.sundroid.kobokobo.admin.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sundroid.kobokobo.admin.data.domain.model.Department
import com.sundroid.kobokobo.admin.data.domain.repository.DepartmentRepository
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.utils.StringUtil.BUSINESS_COLLECTION
import com.sundroid.kobokobo.utils.StringUtil.DEPARTMENTS_COLLECTION
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class DefaultDepartmentRepository(
    private val firestore: FirebaseFirestore
) : DepartmentRepository
{



    override suspend fun createDepartment(department: Department): Result<Department, DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(department.businessId)
        val docRef = busRef.collection(DEPARTMENTS_COLLECTION).document(department.id)
        docRef.set(department).await()
        department
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override suspend fun updateDepartment(department: Department): Result<Department, DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(department.businessId)
        val docRef = busRef.collection(DEPARTMENTS_COLLECTION).document(department.id)
        val updated = department.copy(updatedAt = System.currentTimeMillis())
        docRef.set(updated).await()
        updated
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override suspend fun deleteDepartment(departmentId: String, businessId: String): EmptyResult<DataError> = runCatching {
        // Ensure doc belongs to the business (best-effort check)
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
        val doc = busRef.collection(DEPARTMENTS_COLLECTION).document(departmentId).get().await()
        val belongs = doc.exists()
        if (!belongs) throw IllegalStateException("Department not found or not in business")
        firestore.collection(DEPARTMENTS_COLLECTION).document(departmentId).delete().await()
    }.fold(
        onSuccess = { Result.Success(Unit) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override suspend fun getDepartmentById(departmentId: String, businessId: String): Result<Department?, DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
        val doc = busRef.collection(DEPARTMENTS_COLLECTION).document(departmentId).get().await()
        if (!doc.exists()) null else doc.toObject(Department::class.java)?.takeIf { it.businessId == businessId }
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override fun getDepartments(businessId: String): Flow<List<Department>> = callbackFlow {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
        val query = busRef.collection(DEPARTMENTS_COLLECTION)
            .orderBy("name", Query.Direction.ASCENDING)
        val listener = query.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val departments = snapshot.documents.mapNotNull { it.toObject(Department::class.java) }
            trySend(departments)
        }
        awaitClose { listener.remove() }
    }
}


