package com.sundroid.kobokobo.dashboard.data.mappers


import com.sundroid.kobokobo.dashboard.data.database.CategoryEntity
import com.sundroid.kobokobo.dashboard.data.database.ItemEntity
import com.sundroid.kobokobo.dashboard.data.dto.CategoryDto
import com.sundroid.kobokobo.dashboard.data.dto.ItemDto
import com.sundroid.kobokobo.dashboard.domain.Category
import com.sundroid.kobokobo.inventory.data.domain.model.Product


fun Product.toItemEntity(): ItemEntity {
    return ItemEntity(
        id = id,
        name = name,
        image = null,
        costPrice = costPrice,
        unitPrice = unitPrice,
        categoryId = categoryId,
        discount = discount?.toDouble(),
        vat = vat.toDouble(),
        sku = sku,
        stock = stock ?: 0.0,
        sortOrder = sortOrder
    )
}
fun ItemEntity.toItem(): Product{
    return Product(
        id = id,
        name = name,
       // image = image,
        costPrice = costPrice,
        unitPrice = unitPrice,
        categoryId = categoryId,
        discount = discount?.toDouble(),
        vat = vat.toDouble(),
        sku = sku,
        stock = stock,
        sortOrder = sortOrder
    )
}
fun ItemDto.toItem(): Product{
    return Product(
        id = id,
        name = name,
        costPrice = costPrice.toDouble(),
       /// image = image,
        unitPrice = unitPrice.toDouble(),
        categoryId = categoryId,
        discount = discount?.toDouble(),
        vat = vat.toDouble(),
        sku = sku,
        stock = stock?.toDoubleOrNull() ?: 0.0,
        sortOrder = sortOrder?.toIntOrNull() ?: 0
    )
}

fun Category.toCategoryEntity(): CategoryEntity {
    return CategoryEntity(
       categoryId = categoryId,
        categoryName =  categoryName,
        categoryCode = categoryCode
    )
}
fun CategoryEntity.toCategory(): Category{
    return Category(
        categoryId = categoryId,
        categoryName =  categoryName,
        categoryCode = categoryCode
    )
}
fun CategoryDto.toCategory(): Category{
    return Category(
        categoryId = categoryId,
        categoryName =  categoryName,
        categoryCode = categoryCode
    )
}