package com.ikemba.inventrar.cart.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.ikemba.inventrar.cart.data.database.CartDao
import com.ikemba.inventrar.cart.data.database.CartEntity
import com.ikemba.inventrar.cart.data.database.PostSalesRequestDao
import com.ikemba.inventrar.cart.data.database.PostSalesRequestEntity
import com.ikemba.inventrar.cart.data.dto.PostSalesRequest
import com.ikemba.inventrar.cart.data.dto.ReceiptDataDto
import com.ikemba.inventrar.cart.data.dto.ReceiptItemDto
import com.ikemba.inventrar.cart.data.dto.ReceiptResponseDto
import com.ikemba.inventrar.cart.data.dto.ReceiptSummaryDto
import com.ikemba.inventrar.cart.domain.CartRepository
import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.EmptyResult
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class DefaultCartRepository(
    private val firestore: FirebaseFirestore,
    private val cartDao: CartDao,
    private val postSalesRequestDao: PostSalesRequestDao
): CartRepository {


    override suspend fun postCartSales(postSalesRequest: PostSalesRequest):Result<ResponseDto, DataError.Remote> {
        return try {
            val ref = postSalesRequest.reference ?: System.currentTimeMillis().toString()
            println("barry 1")
            val data = hashMapOf(
                "reference" to ref,
                "cart" to (postSalesRequest.cart ?: emptyList()),
                "total" to (postSalesRequest.total ?: 0.0),
                "total_vat" to (postSalesRequest.total_vat ?: 0.0),
                "grandTotal" to (postSalesRequest.grandTotal ?: 0.0),
                "payment_method" to (postSalesRequest.payment_method ?: "cash"),
                "is_held" to postSalesRequest.is_held,
                "createdAt" to FieldValue.serverTimestamp()
            )
            println("barry 2")
            firestore.collection("sales").document(ref).set(data).await()
            println("barry 3")
            Result.Success(ResponseDto(responseCode = 0, responseMessage = "OK"))
        } catch (e: Exception) {
            println("barry 4 "+ e.message)
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun getOrderReceipt(
        voidOrderRequest: VoidOrderRequest
    ): Result<ReceiptResponseDto?, DataError> {
        return try {
            println("doku "+ voidOrderRequest.reference)
            val doc = firestore.collection("sales").document(voidOrderRequest.reference).get().await()
            if (!doc.exists()) {
                println("aminu no")
                return Result.Success(
                    ReceiptResponseDto(
                        responseCode = 20,
                        responseMessage = "Not Found",
                        data = null
                    )
                )
            }
            println("aminu yes")
            val items = (doc.get("cart") as? List<Map<String, Any?>>)?.map { m ->
                ReceiptItemDto(
                    productId = (m["product_id"]?.toString()),
                    productName = (m["product_name"]?.toString()),
                    unitPrice = (m["subtotal"] as? Number)?.toDouble()?.toString() ?: "0.0",
                    quantity = (m["qty"]?.toString() ?: "1"),
                    unitVat = (m["vat_amount"] as? Number)?.toDouble()?.toString() ?: "0.0",
                    unitDiscount = (m["discount"]?.toString() ?: "0.0"),
                    subTotal = (m["subtotal"] as? Number)?.toDouble()?.toString() ?: "0.0"
                )
            } ?: emptyList()
            println("grealish "+ items.size)
            val grandTotal = (doc.get("grandTotal") as? Number)?.toDouble() ?: 0.0
            val totalVat = (doc.get("total_vat") as? Number)?.toDouble() ?: 0.0
            val summary = listOf(
                ReceiptSummaryDto(
                    created = "",
                    orderNo = voidOrderRequest.reference,
                    actionTime = "",
                    taxTotal = totalVat.toString(),
                    discountTotal = "0.0",
                    subTotal = grandTotal.toString(),
                    grandTotal = grandTotal.toString(),
                    salesPerson = "",
                    businessName = "",
                    logo = "",
                    businessAddress = "",
                    vatRate = "",
                    currencyCode = "",
                    currencyEntity = ""
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

}