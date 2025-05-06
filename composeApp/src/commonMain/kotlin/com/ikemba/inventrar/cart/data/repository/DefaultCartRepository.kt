package com.ikemba.inventrar.cart.data.repository

import com.ikemba.inventrar.cart.data.database.CartDao
import com.ikemba.inventrar.cart.data.database.CartEntity
import com.ikemba.inventrar.cart.data.database.PostSalesRequestDao
import com.ikemba.inventrar.cart.data.database.PostSalesRequestEntity
import com.ikemba.inventrar.cart.data.dto.PostSalesRequest
import com.ikemba.inventrar.cart.data.dto.ReceiptResponseDto
import com.ikemba.inventrar.cart.data.network.RemoteCartDataSource
import com.ikemba.inventrar.cart.domain.CartRepository
import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.EmptyResult
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest
import kotlinx.coroutines.flow.Flow

class DefaultCartRepository(
    private val remoteCartDataSource: RemoteCartDataSource,
    private val cartDao: CartDao,
    private val postSalesRequestDao: PostSalesRequestDao
): CartRepository {


    override suspend fun postCartSales(accessToken: String, postSalesRequest: PostSalesRequest):Result<ResponseDto, DataError.Remote> {
        return remoteCartDataSource
            .postSales(accessToken, postSalesRequest)
    }

    override suspend fun getOrderReceipt(
        accessToken: String,
        voidOrderRequest: VoidOrderRequest
    ): Result<ReceiptResponseDto?, DataError> {
        return remoteCartDataSource.getOrderReceipt(accessToken,voidOrderRequest)
    }

    override suspend fun holdOrder(accessToken: String, postSalesRequest: PostSalesRequest):Result<ResponseDto, DataError.Remote> {
        return remoteCartDataSource
            .holdOrder(accessToken, postSalesRequest)
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