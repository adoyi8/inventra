package com.ikemba.inventrar.inventory.network


import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.data.safeCall
import com.ikemba.inventrar.core.domain.DataError

import com.ikemba.inventrar.inventory.data.domain.InventoryItem
import com.ikemba.inventrar.inventory.data.domain.InventoryRepository
import io.ktor.client.HttpClient
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.dashboard.utils.Util.APP_NAME
import com.ikemba.inventrar.dashboard.utils.Util.AUTHORIZATION_TOKEN
import com.ikemba.inventrar.dashboard.utils.Util.INVENTORY_SERVICE_URL
import com.ikemba.inventrar.dashboard.utils.Util.ORGANIZATION_SERVICE_URL
import com.ikemba.inventrar.dashboard.utils.Util.accessToken
import com.ikemba.inventrar.inventory.data.dto.InventoryDto
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.utils.io.InternalAPI
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class KtorRemoteInventoryDataSource(
    private val httpClient: HttpClient
): InventoryRepository {
    @OptIn(InternalAPI::class)
    override suspend fun createInventory(
        sku: String,
        itemName: String,
        category: String?,
        unitOfMeasure: String?,
        quantity: Double?,
        purchasePricePerUnit: Double?,
        sellingPricePerUnit: Double?,
        imageUrl: String?,
        reorderLevel: Double?,
        organizationId: String,
        createdBy: String
    ): Result<ResponseDto, DataError.Remote> {
        return safeCall<ResponseDto> {
      val request = InventoryDto(
                sku = sku,
                itemName = itemName,
                category = category,
                unitOfMeasure = unitOfMeasure,
                quantity = quantity!!.toDouble(),
                purchasePricePerUnit = purchasePricePerUnit,
                sellingPricePerUnit = sellingPricePerUnit,
                imageUrl = imageUrl,
                reorderLevel = reorderLevel,
                organizationId = organizationId,
                createdBy = createdBy,
                itemId = "",
                itemDisplayId = "",
                organizationName = "",
                status = "",
                updatedBy = "",
                voidedBy = "",
                voided = false

            )
            val json = Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            }

            // 4. Encode the object to a JSON string
            val jsonString = json. encodeToString<InventoryDto>(request)
            httpClient.post(
                urlString = "${INVENTORY_SERVICE_URL}/create-inventory"
            ){
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                    append("Authentication", AUTHORIZATION_TOKEN)
                    append("appname", APP_NAME)
                }
                setBody(


                    MultiPartFormDataContent(
                        formData {
                            // Part 1: The JSON request payload
                            append("request", jsonString, Headers.build {
                                append(HttpHeaders.ContentType, "application/json")
                            })
                          //  append("request", jsonString, ContentType.Application.Json)

                            // Part 2: The image file (if provided)
//                            if (imageBytes != null && !imageBytes.isEmpty() && imageFileName != null) {
//                                append("imageFile", imageBytes, Headers.build {
//                                    append(HttpHeaders.ContentType, getContentType(imageFileName)) // Dynamically determine content type
//                                    append(HttpHeaders.ContentDisposition, "filename=\"$imageFileName\"")
//                                })
//                            }
                        }
                    )
                )

            }
        }
    }

    override suspend fun searchInventory(
        itemId: String?,
        sku: String?,
        itemName: String?,
        category: String?,
        organizationId: String,
        voided: Boolean?,
        status: String?
    ): Result<List<InventoryItem>, DataError.Remote> {
        TODO("Not yet implemented")
    }

    override suspend fun updateInventory(
        itemId: String,
        sku: String?,
        itemName: String?,
        category: String?,
        unitOfMeasure: String?,
        quantity: Double?,
        purchasePricePerUnit: Double?,
        sellingPricePerUnit: Double?,
        imageUrl: String?,
        reorderLevel: Double?,
        updatedBy: String
    ): Result<ResponseDto,DataError.Remote> {
        TODO("Not yet implemented")
    }

    override suspend fun updateInventoryQuantity(
        itemId: String,
        newQuantity: Double,
        updatedBy: String
    ): Result<ResponseDto, DataError.Remote> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteInventory(
        itemId: String,
        voidedBy: String
    ):Result<ResponseDto, DataError.Remote> {
        TODO("Not yet implemented")
    }

    override suspend fun activateInventory(
        itemId: String,
        updatedBy: String
    ): Result<ResponseDto, DataError.Remote> {
        TODO("Not yet implemented")
    }

    override suspend fun deactivateInventory(
        itemId: String,
        updatedBy: String
    ): Result<ResponseDto, DataError.Remote> {
        TODO("Not yet implemented")
    }


}