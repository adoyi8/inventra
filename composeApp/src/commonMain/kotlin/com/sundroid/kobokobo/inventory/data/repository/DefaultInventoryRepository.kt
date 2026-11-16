package com.sundroid.kobokobo.inventory.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sundroid.kobokobo.inventory.data.domain.model.*
import com.sundroid.kobokobo.inventory.data.domain.repository.InventoryRepository
import com.sundroid.kobokobo.audit.data.domain.model.ActionType
import com.sundroid.kobokobo.audit.data.domain.model.AuditTrail
import com.sundroid.kobokobo.audit.data.domain.model.EntityType
import com.sundroid.kobokobo.audit.data.domain.repository.AuditTrailRepository
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.utils.StringUtil.BUSINESS_COLLECTION
import com.sundroid.kobokobo.utils.StringUtil.CATEGORIES_COLLECTION
import com.sundroid.kobokobo.utils.StringUtil.LOCATIONS_COLLECTION
import com.sundroid.kobokobo.utils.StringUtil.PRODUCTS_COLLECTION
import com.sundroid.kobokobo.utils.StringUtil.STOCK_LEVELS_COLLECTION
import com.sundroid.kobokobo.utils.StringUtil.STOCK_MOVEMENTS_COLLECTION
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class DefaultInventoryRepository(
    private val firestore: FirebaseFirestore,
    private val auditTrailRepository: AuditTrailRepository
) : InventoryRepository {

    // Product operations
    override suspend fun createProduct(product: Product): Result<Product, DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(product.businessId)
        val docRef = busRef.collection(PRODUCTS_COLLECTION).document(product.id)
        docRef.set(product).await()
        
        // Log Audit Trail
        auditTrailRepository.createAuditTrail(
            AuditTrail(
                businessId = product.businessId,
                staffId = "SYSTEM", // Or passed staffId if available in Product
                actionType = ActionType.CREATE,
                entityType = EntityType.PRODUCT,
                entityId = product.id,
                auditTrailMessage = "Product Created: ${product.name}",
                productId = product.id,
                categoryId = product.categoryId,
                newValues = mapOf("stock" to (product.stock ?: 0.0), "price" to product.unitPrice)
            )
        )
        
        product
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override suspend fun updateProduct(product: Product): Result<Product, DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(product.businessId)
        val docRef = busRef.collection(PRODUCTS_COLLECTION).document(product.id)
        val updated = product.copy(updatedAt = System.currentTimeMillis())
        docRef.set(updated).await()
        
        // Log Audit Trail
        auditTrailRepository.createAuditTrail(
            AuditTrail(
                businessId = product.businessId,
                staffId = "SYSTEM", // Or passed staffId
                actionType = ActionType.UPDATE,
                entityType = EntityType.PRODUCT,
                entityId = product.id,
                auditTrailMessage = "Product Updated: ${product.name}",
                productId = product.id,
                newValues = mapOf("stock" to (product.stock ?: 0.0), "price" to product.unitPrice)
            )
        )
        
        updated
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override suspend fun deleteProduct(productId: String, businessId: String): EmptyResult<DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
        val doc = busRef.collection(PRODUCTS_COLLECTION).document(productId).get().await()
        val belongs = doc.exists()
        if (!belongs) throw IllegalStateException("Product not found or not in business")
        firestore.collection(PRODUCTS_COLLECTION).document(productId).delete().await()
    }.fold(
        onSuccess = { Result.Success(Unit) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override suspend fun getProductById(productId: String, businessId: String): Result<Product?, DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
        val doc = busRef.collection(PRODUCTS_COLLECTION).document(productId).get().await()
        if (!doc.exists()) null else doc.toObject(Product::class.java)?.takeIf { it.businessId == businessId }
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override fun getProducts(businessId: String): Flow<List<Product>> = callbackFlow {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
        val query = busRef.collection(PRODUCTS_COLLECTION)
            .orderBy("name", Query.Direction.ASCENDING)
        val listener = query.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val products = snapshot.documents.mapNotNull { it.toObject(Product::class.java) }
            trySend(products)
        }
        awaitClose { listener.remove() }
    }

    override fun getProductsByCategory(categoryId: String, businessId: String): Flow<List<Product>> = callbackFlow {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
        val query = busRef.collection(PRODUCTS_COLLECTION)
            .whereEqualTo("categoryId", categoryId)
            .orderBy("name", Query.Direction.ASCENDING)
        val listener = query.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val products = snapshot.documents.mapNotNull { it.toObject(Product::class.java) }
            trySend(products)
        }
        awaitClose { listener.remove() }
    }

    // Category operations
    override suspend fun createCategory(category: Category): Result<Category, DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(category.businessId)
        val docRef = busRef.collection(CATEGORIES_COLLECTION).document(category.id)
        docRef.set(category).await()
        category
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override suspend fun updateCategory(category: Category): Result<Category, DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(category.businessId)
        val docRef = busRef.collection(CATEGORIES_COLLECTION).document(category.id)
        val updated = category.copy(updatedAt = System.currentTimeMillis())
        docRef.set(updated).await()
        updated
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override suspend fun deleteCategory(categoryId: String, businessId: String): EmptyResult<DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
        val doc = busRef.collection(CATEGORIES_COLLECTION).document(categoryId).get().await()
        val belongs = doc.exists()
        if (!belongs) throw IllegalStateException("Category not found or not in business")
        firestore.collection(CATEGORIES_COLLECTION).document(categoryId).delete().await()
    }.fold(
        onSuccess = { Result.Success(Unit) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override suspend fun getCategoryById(categoryId: String, businessId: String): Result<Category?, DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
        val doc = busRef.collection(CATEGORIES_COLLECTION).document(categoryId).get().await()
        if (!doc.exists()) null else doc.toObject(Category::class.java)?.takeIf { it.businessId == businessId }
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override fun getCategories(businessId: String): Flow<List<Category>> = callbackFlow {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
        val query = busRef.collection(CATEGORIES_COLLECTION)
            .orderBy("name", Query.Direction.ASCENDING)
        val listener = query.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val categories = snapshot.documents.mapNotNull { it.toObject(Category::class.java) }
            trySend(categories)
        }
        awaitClose { listener.remove() }
    }

    // Location operations
    override suspend fun createLocation(location: Location): Result<Location, DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(location.businessId)
        val docRef = busRef.collection(LOCATIONS_COLLECTION).document(location.id)
        docRef.set(location).await()
        location
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override suspend fun updateLocation(location: Location): Result<Location, DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(location.businessId)
        val docRef = busRef.collection(LOCATIONS_COLLECTION).document(location.id)
        val updated = location.copy(updatedAt = System.currentTimeMillis())
        docRef.set(updated).await()
        updated
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override suspend fun deleteLocation(locationId: String, businessId: String): EmptyResult<DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
        val doc = busRef.collection(LOCATIONS_COLLECTION).document(locationId).get().await()
        val belongs = doc.exists()
        if (!belongs) throw IllegalStateException("Location not found or not in business")
        firestore.collection(LOCATIONS_COLLECTION).document(locationId).delete().await()
    }.fold(
        onSuccess = { Result.Success(Unit) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override suspend fun getLocationById(locationId: String, businessId: String): Result<Location?, DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
        val doc = busRef.collection(LOCATIONS_COLLECTION).document(locationId).get().await()
        if (!doc.exists()) null else doc.toObject(Location::class.java)?.takeIf { it.businessId == businessId }
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override fun getLocations(businessId: String): Flow<List<Location>> = callbackFlow {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
        val query = busRef.collection(LOCATIONS_COLLECTION)
            .orderBy("name", Query.Direction.ASCENDING)
        val listener = query.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val locations = snapshot.documents.mapNotNull { it.toObject(Location::class.java) }
            trySend(locations)
        }
        awaitClose { listener.remove() }
    }

    // Stock operations
    override suspend fun createStockMovement(movement: StockMovement): Result<StockMovement, DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(movement.businessId)
        val docRef = busRef.collection(STOCK_MOVEMENTS_COLLECTION).document(movement.id)
        docRef.set(movement).await()
        movement
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override suspend fun getStockLevel(productId: String, locationId: String, businessId: String): Result<StockLevel?, DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
        val query = busRef.collection(STOCK_LEVELS_COLLECTION)
            .whereEqualTo("productId", productId)
            .whereEqualTo("locationId", locationId)
            .limit(1)
            .get()
            .await()
        
        query.documents.firstOrNull()?.toObject(StockLevel::class.java)
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override suspend fun updateStockLevel(stockLevel: StockLevel): Result<StockLevel, DataError> = runCatching {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(stockLevel.businessId)
        val docRef = busRef.collection(STOCK_LEVELS_COLLECTION).document(stockLevel.id)
        val updated = stockLevel.copy(lastUpdated = System.currentTimeMillis())
        docRef.set(updated).await()
        updated
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT) }
    )

    override fun getStockLevels(businessId: String): Flow<List<StockLevel>> = callbackFlow {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
        val query = busRef.collection(STOCK_LEVELS_COLLECTION)
            .whereEqualTo("businessId", businessId)
        val listener = query.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val stockLevels = snapshot.documents.mapNotNull { it.toObject(StockLevel::class.java) }
            trySend(stockLevels)
        }
        awaitClose { listener.remove() }
    }

    override fun getStockMovements(productId: String?, businessId: String): Flow<List<StockMovement>> = callbackFlow {
        val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
        val query = if (productId != null) {
            busRef.collection(STOCK_MOVEMENTS_COLLECTION)
                .whereEqualTo("businessId", businessId)
                .whereEqualTo("productId", productId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
        } else {
            busRef.collection(STOCK_MOVEMENTS_COLLECTION)
                .whereEqualTo("businessId", businessId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
        }
        
        val listener = query.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val movements = snapshot.documents.mapNotNull { it.toObject(StockMovement::class.java) }
            trySend(movements)
        }
        awaitClose { listener.remove() }
    }
}









