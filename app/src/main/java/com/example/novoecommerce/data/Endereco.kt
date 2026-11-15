package com.example.novoecommerce.data

data class Endereco(
    val id: Int = 0,
    val apelido: String,
    val cep: String,
    val rua: String,
    val bairro: String,
    val cidade: String,
    val estado: String
)
