package com.example.novoecommerce.data

const val LOREM =
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed porttitor ultricies " +
        "tortor sed pellentesque. Nullam consequat ullamcorper tellus eu hendrerit. " +
        "Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; " +
        "Donec vitae nisi at lacus fermentum tincidunt."

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val category: String,
    val emoji: String,
    val description: String = LOREM
)

data class CartItem(
    val product: Product,
    val quantity: Int
)
