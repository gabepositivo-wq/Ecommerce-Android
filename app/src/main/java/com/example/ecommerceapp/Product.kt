package com.example.ecommerceapp

val products = listOf(
    Product(
        1,
        "Camiseta Básica",
        59.90,
        "Camiseta de algodão",
        ""
    ),
    Product(
        2,
        "Tênis Esportivo",
        199.90,
        "Tênis para corrida",
        ""
    ),
    Product(
        3,
        "Mochila",
        129.90,
        "Mochila para uso diário",
        ""
    )
)

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val imageUrl: String
)