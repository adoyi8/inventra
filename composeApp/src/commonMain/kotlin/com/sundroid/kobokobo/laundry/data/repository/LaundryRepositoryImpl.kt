package com.sundroid.kobokobo.laundry.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.laundry.data.domain.model.LaundryMaterial
import com.sundroid.kobokobo.laundry.data.domain.repository.LaundryRepository
import com.sundroid.kobokobo.utils.StringUtil.BUSINESS_COLLECTION
import com.sundroid.kobokobo.utils.StringUtil.LAUNDRY_COLLECTION
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose

class LaundryRepositoryImpl(
    private val firestore: FirebaseFirestore
) : LaundryRepository {

    private val collectionName = LAUNDRY_COLLECTION

    override fun getLaundryMaterials(businessId: String): Flow<List<LaundryMaterial>> = callbackFlow {
        val query = firestore.collection(BUSINESS_COLLECTION).document(businessId)
            .collection(collectionName)
        val listener = query.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val materials = snapshot.documents.mapNotNull { doc ->
                doc.toObject(LaundryMaterial::class.java)?.copy(id = doc.id)
            }.filter { it.isActive }.sortedByDescending { it.createdAt }
            trySend(materials)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getLaundryMaterialById(businessId: String, id: String): Result<LaundryMaterial, DataError> {
        return try {
            val doc = firestore.collection(BUSINESS_COLLECTION).document(businessId)
                .collection(collectionName).document(id).get().await()
            val material = doc.toObject(LaundryMaterial::class.java)
            if (material != null) {
                Result.Success(material.copy(id = doc.id))
            } else {
                Result.Error(DataError.Remote.SERVER)
            }
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun addLaundryMaterial(businessId: String, material: LaundryMaterial): EmptyResult<DataError> {
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

    override suspend fun updateLaundryMaterial(businessId: String, material: LaundryMaterial): EmptyResult<DataError> {
        return try {
            val updateData = mapOf(
                "name" to material.name,
                "washingPrice" to material.washingPrice,
                "ironingPrice" to material.ironingPrice,
                "washAndIronPrice" to material.washAndIronPrice,
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

    override suspend fun deleteLaundryMaterial(businessId: String, id: String): EmptyResult<DataError> {
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
