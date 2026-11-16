package com.sundroid.kobokobo.services.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.services.data.domain.model.ServicesMaterial
import com.sundroid.kobokobo.services.data.domain.repository.ServicesRepository
import com.sundroid.kobokobo.utils.StringUtil.BUSINESS_COLLECTION
import com.sundroid.kobokobo.utils.StringUtil.SERVICES_COLLECTION
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose

class ServicesRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ServicesRepository {

    private val collectionName = SERVICES_COLLECTION

    override fun getServicesMaterials(businessId: String): Flow<List<ServicesMaterial>> = callbackFlow {
        val query = firestore.collection(BUSINESS_COLLECTION).document(businessId)
            .collection(collectionName)
        val listener = query.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val materials = snapshot.documents.mapNotNull { doc ->
                doc.toObject(ServicesMaterial::class.java)?.copy(id = doc.id)
            }.filter { it.isActive }.sortedByDescending { it.createdAt }
            trySend(materials)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getServicesMaterialById(businessId: String, id: String): Result<ServicesMaterial, DataError> {
        return try {
            val doc = firestore.collection(BUSINESS_COLLECTION).document(businessId)
                .collection(collectionName).document(id).get().await()
            val material = doc.toObject(ServicesMaterial::class.java)
            if (material != null) {
                Result.Success(material.copy(id = doc.id))
            } else {
                Result.Error(DataError.Remote.SERVER)
            }
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun addServicesMaterial(businessId: String, material: ServicesMaterial): EmptyResult<DataError> {
        return try {
            firestore.collection(BUSINESS_COLLECTION).document(businessId).collection(collectionName)
                .document(material.id)
                .set(material)
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun updateServicesMaterial(businessId: String, material: ServicesMaterial): EmptyResult<DataError> {
        return try {
            val updateData = mapOf(
                "name" to material.name,
                "price" to material.price,
                "isActive" to material.isActive,
                "updatedAt" to System.currentTimeMillis(),
                "updatedBy" to material.updatedBy
            )
            firestore.collection(BUSINESS_COLLECTION).document(businessId).collection(collectionName)
                .document(material.id)
                .update(updateData)
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun deleteServicesMaterial(businessId: String, id: String): EmptyResult<DataError> {
        return try {
            firestore.collection(BUSINESS_COLLECTION).document(businessId).collection(collectionName)
                .document(id)
                .update("isActive", false)
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
}
