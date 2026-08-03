package com.sundroid.kobokobo.inventory.data.domain.model

import java.util.UUID



// Function to generate the batch for a new business
fun getDefaultInventory(businessId: String, ownerId: String): Pair<List<Category>, List<Product>> {
    val timestamp = System.currentTimeMillis()
    val categories = mutableListOf<Category>()
    val products = mutableListOf<Product>()

    fun createCategory(name: String): Category {
        val cat = Category(
            id = UUID.randomUUID().toString(),
            name = name,
            description = "Default $name category",
            businessId = businessId,
            isActive = true,
            createdAt = timestamp,
            updatedAt = timestamp,
            createdBy = ownerId,
            updatedBy = ownerId
        )
        categories.add(cat)
        return cat
    }

    val juiceCat = createCategory("Juice / Yoghurt")
    val softDrinksCat = createCategory("Soft Drinks")
    val alcoholCat = createCategory("Alcohol")
    val energyDrinksCat = createCategory("Energy Drinks")
    val maltDrinksCat = createCategory("Malt Drinks")

    fun addProduct(name: String, price: Double, categoryId: String) {
        products.add(Product(
            id = UUID.randomUUID().toString(),
            name = name,
            unitPrice = price,
            costPrice = price,
            categoryId = categoryId,
            stock = 0.0,
            isActive = true,
            businessId = businessId,
            createdAt = timestamp,
            updatedAt = timestamp
        ))
    }

    // === Juice / Yoghurt ===
    addProduct("Hollandia (Big)", 2500.0, juiceCat.id)
    addProduct("Hollandia (Small)", 1500.0, juiceCat.id)
    addProduct("Chi Exotic (Big)", 2500.0, juiceCat.id)
    addProduct("Chi Exotic (Medium)", 1500.0, juiceCat.id)
    addProduct("Chi Exotic (Small)", 1500.0, juiceCat.id)
    addProduct("Chi Active (Big)", 2500.0, juiceCat.id)
    addProduct("Chi Active (Can)", 2500.0, juiceCat.id)
    addProduct("Dudu Mix Juice", 1000.0, juiceCat.id)
    addProduct("Dudu Yoghurt", 1000.0, juiceCat.id)
    addProduct("Nutri Milk", 1000.0, juiceCat.id)
    addProduct("Vita Milk", 2500.0, juiceCat.id)
    addProduct("Dudu Fruit Mix", 1500.0, juiceCat.id)

    // === Soft Drinks ===
    addProduct("Coke (Big)", 600.0, softDrinksCat.id)
    addProduct("Coke (Small)", 500.0, softDrinksCat.id)
    addProduct("Fanta (Big)", 600.0, softDrinksCat.id)
    addProduct("Fanta (Small)", 500.0, softDrinksCat.id)
    addProduct("Sprite (Can)", 1000.0, softDrinksCat.id)
    addProduct("Fayrouz (Can)", 1000.0, softDrinksCat.id)
    addProduct("Schnapps (Can)", 1000.0, softDrinksCat.id)

    // === Alcohol ===
    addProduct("Yahoo gin", 600.0, alcoholCat.id)
    addProduct("Striker", 600.0, alcoholCat.id)
    addProduct("Orijin Bitter (small)", 700.0, alcoholCat.id)
    addProduct("Igboya (big)", 1500.0, alcoholCat.id)
    addProduct("Igboya (small)", 700.0, alcoholCat.id)
    addProduct("Halogen", 700.0, alcoholCat.id)
    addProduct("Oga Abeg", 300.0, alcoholCat.id)
    addProduct("Water Bottle", 400.0, alcoholCat.id)
    addProduct("Origin Bitter (Big)", 1500.0, alcoholCat.id)
    addProduct("Bullet", 0.0, alcoholCat.id)
    addProduct("Smirnoff Ice Beer", 0.0, alcoholCat.id)
    addProduct("Smirnoff Ice Double black (can)", 2500.0, alcoholCat.id)
    addProduct("Desperado", 1500.0, alcoholCat.id)
    addProduct("Stout (Medium)", 1400.0, alcoholCat.id)
    addProduct("Trophy Stout", 1200.0, alcoholCat.id)
    addProduct("Trophy Beer", 1100.0, alcoholCat.id)
    addProduct("Hero", 1100.0, alcoholCat.id)
    addProduct("Goldberg Black", 1200.0, alcoholCat.id)
    addProduct("Goldberg Beer", 1100.0, alcoholCat.id)
    addProduct("Life", 1100.0, alcoholCat.id)
    addProduct("Castle Light", 1100.0, alcoholCat.id)
    addProduct("Legend (Large)", 1300.0, alcoholCat.id)
    addProduct("Heineken", 1100.0, alcoholCat.id)

    // === Energy Drinks ===
    addProduct("Climax", 1000.0, energyDrinksCat.id)
    addProduct("Climax (Can)", 1000.0, energyDrinksCat.id)
    addProduct("Lucozade Boost", 1500.0, energyDrinksCat.id)
    addProduct("Vita Milk Banana", 2500.0, energyDrinksCat.id)
    addProduct("Black Stallion Horse", 2500.0, energyDrinksCat.id)
    addProduct("Toma Mango", 1500.0, energyDrinksCat.id)
    addProduct("Power Horse", 2500.0, energyDrinksCat.id)
    addProduct("Predator", 1000.0, energyDrinksCat.id)
    addProduct("Fearless", 1000.0, energyDrinksCat.id)
    addProduct("Monster Black", 2500.0, energyDrinksCat.id)
    addProduct("Monster Ultra", 2500.0, energyDrinksCat.id)

    // === Malt Drinks ===
    addProduct("Maltina (Can)", 1000.0, maltDrinksCat.id)
    addProduct("Maltina (Pet)", 800.0, maltDrinksCat.id)
    addProduct("Amstel (Can)", 1000.0, maltDrinksCat.id)
    addProduct("Dubic (Can)", 1000.0, maltDrinksCat.id)

    return Pair(categories, products)
}