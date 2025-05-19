package com.ikemba.inventrar.dashboard.data.mappers


import com.ikemba.inventrar.dashboard.data.database.CategoryEntity
import com.ikemba.inventrar.dashboard.data.database.ItemEntity
import com.ikemba.inventrar.dashboard.data.dto.CategoryDto
import com.ikemba.inventrar.dashboard.data.dto.ItemDto
import com.ikemba.inventrar.dashboard.domain.Category
import com.ikemba.inventrar.dashboard.domain.Item



fun Item.toItemEntity(): ItemEntity {
    return ItemEntity(
        id = id,
        name = name,
        image = image,
        unitPrice = unitPrice,
        categoryId = categoryId,
        discount = discount?.toDouble(),
        vat = vat.toDouble(),
        sku = sku
    )
}
fun ItemEntity.toItem(): Item{
    return Item(
        id = id,
        name = name,
        image = image,
        unitPrice = unitPrice,
        categoryId = categoryId,
        discount = discount?.toDouble(),
        vat = vat.toDouble(),
        sku = sku
    )
}
fun ItemDto.toItem(): Item{
    return Item(
        id = id,
        name = name,
        image = image,
        unitPrice = unitPrice.toDouble(),
        categoryId = categoryId,
        discount = discount?.toDouble(),
        vat = vat.toDouble(),
        sku = sku
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