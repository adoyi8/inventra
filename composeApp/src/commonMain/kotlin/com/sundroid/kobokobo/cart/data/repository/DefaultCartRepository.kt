package com.sundroid.kobokobo.cart.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sundroid.kobokobo.audit.data.domain.model.ActionType
import com.sundroid.kobokobo.audit.data.domain.model.AuditTrail
import com.sundroid.kobokobo.audit.data.domain.model.EntityType
import com.sundroid.kobokobo.audit.data.domain.repository.AuditTrailRepository
import com.sundroid.kobokobo.cart.data.database.CartDao
import com.sundroid.kobokobo.cart.data.database.CartEntity
import com.sundroid.kobokobo.cart.data.database.HeldCartDao
import com.sundroid.kobokobo.cart.data.database.HeldCartEntity
import com.sundroid.kobokobo.cart.data.database.PostSalesRequestDao
import com.sundroid.kobokobo.cart.data.database.PostSalesRequestEntity
import com.sundroid.kobokobo.cart.data.dto.PostSalesRequest
import com.sundroid.kobokobo.cart.data.dto.ReceiptDataDto
import com.sundroid.kobokobo.cart.data.dto.ReceiptItemDto
import com.sundroid.kobokobo.cart.data.dto.ReceiptResponseDto
import com.sundroid.kobokobo.cart.data.dto.ReceiptSummaryDto
import com.sundroid.kobokobo.cart.domain.CartRepository
import com.sundroid.kobokobo.core.data.dto.ResponseDto
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.core.domain.onError
import com.sundroid.kobokobo.core.domain.onSuccess
import com.sundroid.kobokobo.core.domain.toFirebaseError
import com.sundroid.kobokobo.heldOrder.data.dto.VoidOrderRequest
import com.sundroid.kobokobo.utils.StringUtil.BUSINESS_COLLECTION
import com.sundroid.kobokobo.utils.StringUtil.PRODUCTS_COLLECTION
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class DefaultCartRepository(
    private val firestore: FirebaseFirestore,
    private val cartDao: CartDao,
    private val postSalesRequestDao: PostSalesRequestDao,
    private val auditTrailRepository: AuditTrailRepository,
    private val heldCartDao: HeldCartDao
): CartRepository
{


    override suspend fun postCartSales(postSalesRequest: PostSalesRequest):Result<ResponseDto, DataError.Remote> {
        return try {
            val ref = postSalesRequest.reference ?: System.currentTimeMillis().toString()
            val busRef = firestore.collection(BUSINESS_COLLECTION).document(postSalesRequest.businessId)
            println("barry 1 "+postSalesRequest.userId)
            val data = hashMapOf(
                "reference" to ref,
                "cart" to (postSalesRequest.cart ?: emptyList()).map { item ->
                    // Convert PostSalesItem to a Map for Firestore
                    mapOf(
                        "product_id" to item.product_id,
                        "product_name" to item.product_name,
                        "qty" to item.qty,
                        "subtotal" to item.subtotal,
                        "vat_rate" to item.vat_rate,
                        "vat_amount" to item.vat_amount,
                        "discount" to item.discount,
                        "costPrice" to item.costPrice, // <--- NEW: Save costPrice per item
                        "profit" to item.profit // <--- NEW: Save profit per item
                    )
                },
                "total" to (postSalesRequest.total ?: 0.0),
                "total_vat" to (postSalesRequest.total_vat ?: 0.0),
                "grandTotal" to (postSalesRequest.grandTotal ?: 0.0),
                "total_profit" to (postSalesRequest.total_profit ?: 0.0), // <--- NEW: Save total_profit
                "payment_method" to postSalesRequest.payment_method,
                "payment_method_text" to postSalesRequest.payment_method_text,
                "is_paid" to postSalesRequest.is_paid,
                "payment_processed_by" to postSalesRequest.payment_processed_by,
                "is_held" to postSalesRequest.is_held,
                "createdAt" to FieldValue.serverTimestamp(),
                "createdBy" to postSalesRequest.userId
            )
            println("barry 2")
            busRef.collection("sales").document(ref).set(data).await()
            println("barry 3")

            postSalesRequest.cart?.forEach { cartItem ->
                // Subtract stock from Firebase
                if (cartItem.product_id != null) {
                    val productRef = firestore.collection(BUSINESS_COLLECTION)
                        .document(postSalesRequest.businessId)
                        .collection(PRODUCTS_COLLECTION)
                        .document(cartItem.product_id)
                    
                    productRef.update("stock", FieldValue.increment(-(cartItem.qty?.toDouble() ?: 0.0))).await()
                }

                val auditTrail = AuditTrail(
                    businessId = postSalesRequest.businessId,
                    staffId = postSalesRequest.userId,
                    actionType = ActionType.SALE,
                    entityType = EntityType.PRODUCT,
                    entityId = cartItem.product_id ?: "",
                    auditTrailMessage = "${postSalesRequest.userName} Sold ${cartItem.qty} of ${cartItem.product_name}",
                    productId = cartItem.product_id,
                    transactionId = ref,
                    newValues = mapOf(
                        "quantity" to (cartItem.qty ?: 0),
                        "subtotal" to (cartItem.subtotal ?: 0.0)
                    )

                )
                println("Newcastle "+ auditTrail.auditTrailMessage)
                auditTrailRepository.createAuditTrail(auditTrail).onSuccess {
                    println("Audi "+ auditTrail.auditTrailMessage)
                }.onError { error->
                    println("Pep "+ error.toFirebaseError())
                }
            }

            Result.Success(ResponseDto(responseCode = 0, responseMessage = "OK"))
        } catch (e: Exception) {
            println("barry 4 "+ e.message)
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun deleteCartSales(voidOrderRequest: VoidOrderRequest): Result<ResponseDto?, DataError> {
        return try {
            val busRef = firestore.collection(BUSINESS_COLLECTION).document(voidOrderRequest.businessId)
            val docRef = busRef.collection("sales").document(voidOrderRequest.reference)
            val doc = docRef.get().await()

            if (doc.exists()) {
                val cartItems = doc.get("cart") as? List<Map<String, Any?>> ?: emptyList()
                
                // Add stock back to Firebase
                cartItems.forEach { cartItem ->
                    val productId = cartItem["product_id"]?.toString()
                    val qty = (cartItem["qty"] as? Number)?.toDouble() ?: 0.0
                    val productName = cartItem["product_name"]?.toString() ?: "Unknown Product"
                    
                    if (productId != null) {
                        val productRef = firestore.collection(BUSINESS_COLLECTION)
                            .document(voidOrderRequest.businessId)
                            .collection(PRODUCTS_COLLECTION)
                            .document(productId)
                        
                        productRef.update("stock", FieldValue.increment(qty)).await()
                        
                        val auditTrail = AuditTrail(
                            businessId = voidOrderRequest.businessId,
                            staffId = voidOrderRequest.userId, // Requires userId in VoidOrderRequest or fetched from somewhere. Since VoidOrderRequest might not have it, we use SYSTEM
                            actionType = ActionType.DELETE,
                            entityType = EntityType.PRODUCT,
                            entityId = productId,
                            auditTrailMessage = "Reversed Sale: Restored $qty of $productName",
                            productId = productId,
                            transactionId = voidOrderRequest.reference,
                            newValues = mapOf(
                                "quantity" to qty
                            )
                        )
                        auditTrailRepository.createAuditTrail(auditTrail)
                    }
                }
                
                // Delete the sale document
                docRef.delete().await()
                
                // Also delete from local db if needed
                postSalesRequestDao.deleteSales(voidOrderRequest.reference)
                
                Result.Success(ResponseDto(responseCode = 0, responseMessage = "Sale deleted and stock restored"))
            } else {
                Result.Success(ResponseDto(responseCode = 20, responseMessage = "Sale Not Found"))
            }
        } catch (e: Exception) {
            println("deleteCartSales error: "+ e.message)
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun markSalePaid(
        businessId: String,
        reference: String,
        paymentMethod: String,
        paymentMethodText: String,
        processedBy: String
    ): Result<ResponseDto?, DataError> = try {
        firestore.collection(BUSINESS_COLLECTION).document(businessId).collection("sales").document(reference)
            .update(
                mapOf(
                    "payment_method" to paymentMethod,
                    "payment_method_text" to paymentMethodText,
                    "is_paid" to true,
                    "payment_processed_by" to processedBy
                )
            ).await()
        Result.Success(ResponseDto(responseCode = 0, responseMessage = "Payment recorded"))
    } catch (e: Exception) {
        Result.Error(DataError.Remote.UNKNOWN)
    }

    override suspend fun getOrderReceipt(
        voidOrderRequest: VoidOrderRequest
    ): Result<ReceiptResponseDto?, DataError> {
        return try {
            val busRef = firestore.collection(BUSINESS_COLLECTION).document(voidOrderRequest.businessId)
            val doc = busRef.collection("sales").document(voidOrderRequest.reference).get().await()

            if (!doc.exists()) {
                return Result.Success(
                    ReceiptResponseDto(
                        responseCode = 20,
                        responseMessage = "Not Found",
                        data = null
                    )
                )
            }

            val items = (doc.get("cart") as? List<Map<String, Any?>>)?.map { m ->
                ReceiptItemDto(
                    productId = (m["product_id"]?.toString()),
                    productName = (m["product_name"]?.toString()),
                    unitPrice = (m["subtotal"] as? Number)?.toDouble()?.toString() ?: "0.0", // This is subtotal, not unitPrice
                    quantity = (m["qty"]?.toString() ?: "1"),
                    unitVat = (m["vat_amount"] as? Number)?.toDouble()?.toString() ?: "0.0",
                    unitDiscount = (m["discount"]?.toString() ?: "0.0"),
                    subTotal = (m["subtotal"] as? Number)?.toDouble()?.toString() ?: "0.0",
                    costPrice = (m["costPrice"] as? Number)?.toDouble()?.toString() ?: "0.0", // <--- NEW
                    profit = (m["profit"] as? Number)?.toDouble()?.toString() ?: "0.0" // <--- NEW
                )
            } ?: emptyList()

            val grandTotal = (doc.get("grandTotal") as? Number)?.toDouble() ?: 0.0
            val totalVat = (doc.get("total_vat") as? Number)?.toDouble() ?: 0.0
            val totalProfit = (doc.get("total_profit") as? Number)?.toDouble() ?: 0.0 // <--- NEW

            val summary = listOf(
                ReceiptSummaryDto(
                    created = "",
                    orderNo = voidOrderRequest.reference,
                    actionTime = "",
                    taxTotal = totalVat.toString(),
                    discountTotal = "0.0", // You need to calculate total discount if not already saved
                    subTotal = grandTotal.toString(), // This is actually total before VAT if grandTotal includes VAT
                    grandTotal = grandTotal.toString(),
                    salesPerson = "",
                    businessName = "",
                    logo = "",
                    businessAddress = "",
                    vatRate = "",
                    currencyCode = "",
                    currencyEntity = "",
                    totalProfit = totalProfit.toString() // <--- NEW
                )
            )
            Result.Success(
                ReceiptResponseDto(
                    responseCode = 0,
                    responseMessage = "OK",
                    data = ReceiptDataDto(items = items, summary = summary)
                )
            )
        } catch (e: Exception) {
            println("auba "+ e.message)
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun holdOrder(accessToken: String, postSalesRequest: PostSalesRequest):Result<ResponseDto, DataError.Remote> {
        return try {
            val ref = postSalesRequest.reference ?: System.currentTimeMillis().toString()
            val data = hashMapOf(
                "reference" to ref,
                "cart" to (postSalesRequest.cart ?: emptyList()),
                "total" to (postSalesRequest.total ?: 0.0),
                "total_vat" to (postSalesRequest.total_vat ?: 0.0),
                "grandTotal" to (postSalesRequest.grandTotal ?: 0.0),
                "payment_method" to (postSalesRequest.payment_method ?: "cash"),
                "is_held" to true,
                "createdAt" to FieldValue.serverTimestamp()
            )
            firestore.collection("held_orders").document(ref).set(data).await()
            Result.Success(ResponseDto(responseCode = 0, responseMessage = "OK"))
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun saveCart(cart: CartEntity): EmptyResult<DataError.Local> {
        return try {
            cartDao.saveCart(cart)
            Result.Success(Unit)
        } catch(e: Exception) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteCart(cartId: String): EmptyResult<DataError.Local> {
        return try {
            cartDao.deleteCartItem(cartId)
            Result.Success(Unit)
        } catch(e: Exception) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun updateCart(cart: CartEntity): EmptyResult<DataError.Local> {
        return try {
            cartDao.updateCartItem(cart)
            Result.Success(Unit)
        } catch(e: Exception) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun saveSales(salesRequestEntity: PostSalesRequestEntity): EmptyResult<DataError.Local> {
        return try{
            postSalesRequestDao.saveSales(salesRequestEntity)
            Result.Success(Unit)
        }
        catch (e: Exception){
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteSales(reference: String): EmptyResult<DataError.Local> {
        return try{
            postSalesRequestDao.deleteSales(reference)
            Result.Success(Unit)
        }
        catch (e: Exception){
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override fun getAllCarts(): Flow<List<CartEntity>> {
        return cartDao.getAllCartItems()
    }

    override fun getAllSales(): Flow<List<PostSalesRequestEntity>> {
        return postSalesRequestDao.getAllSales()
    }
    override suspend fun saveHeldCart(heldCart: HeldCartEntity): EmptyResult<DataError.Local> {
        return try {
            heldCartDao.insertHeldCart(heldCart)
            Result.Success(Unit)
        } catch (e: Exception) {
            println("Timber "+ e.message)
            Result.Error(DataError.Local.DISK_FULL) // Or a more appropriate error
        }
    }

    override suspend fun deleteHeldCart(heldCart: HeldCartEntity): EmptyResult<DataError.Local> {
        return try {
            heldCartDao.deleteHeldCart(heldCart)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override fun getAllHeldCarts(): Flow<List<HeldCartEntity>> {
        return heldCartDao.getAllHeldCarts()
    }
}
