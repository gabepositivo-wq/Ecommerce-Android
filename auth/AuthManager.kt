package com.example.novoecommerce.auth

object AuthManager {
    data class User(
        val email: String,
        val senha: String,
        val nome: String = ""
    )

    private val usuarios = mutableListOf<User>()

    fun login(email: String, senha: String): Boolean {
        return usuarios.any { it.email == email && it.senha == senha }
    }

    fun cadastrar(nome: String, email: String, senha: String): Boolean {
        if (usuarios.any { it.email == email }) return false
        usuarios.add(User(email, senha, nome))
        return true
    }
}
