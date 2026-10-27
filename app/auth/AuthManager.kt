package com.example.novoecommerce.auth

import android.content.Context
import com.example.novoecommerce.data.dal.UsuarioDAO
import com.example.novoecommerce.validation.CadastroValidator
import java.security.MessageDigest
import java.security.SecureRandom

object AuthManager {

    private fun gerarSalt(): ByteArray =
        ByteArray(16).also { SecureRandom().nextBytes(it) }

    private fun hashSenha(senha: String, salt: ByteArray): ByteArray =
        MessageDigest.getInstance("SHA-256")
            .apply { update(salt) }
            .digest(senha.toByteArray(Charsets.UTF_8))

    fun login(context: Context, email: String, senha: String): Boolean {
        val salvo = UsuarioDAO(context).buscarSenha(email) ?: return false
        val hash = hashSenha(senha, salvo.salt)
        return hash.contentEquals(salvo.hash)
    }

    fun cadastrar(context: Context, nome: String, email: String, senha: String) {
        CadastroValidator.validar(nome, email, senha)
        val salt = gerarSalt()
        val hash = hashSenha(senha, salt)
        UsuarioDAO(context).cadastrar(nome, email, hash, salt)
    }
}
