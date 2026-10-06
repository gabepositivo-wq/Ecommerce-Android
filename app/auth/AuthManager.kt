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

    /**
     * Tenta autenticar o usuário.
     * Retorna `true` se o par e-mail/senha for válido, `false` caso contrário.
     */
    fun login(context: Context, email: String, senha: String): Boolean {
        val salvo = UsuarioDAO(context).buscarSenha(email) ?: return false
        val hash = hashSenha(senha, salvo.salt)
        return hash.contentEquals(salvo.hash)
    }

    /**
     * Cadastra um novo usuário após validar os dados com [CadastroValidator].
     * Lança [IllegalArgumentException] se a validação falhar.
     * Lança [android.database.sqlite.SQLiteConstraintException] se o e-mail já existir.
     */
    fun cadastrar(context: Context, nome: String, email: String, senha: String) {
        CadastroValidator.validar(nome, email, senha)
        val salt = gerarSalt()
        val hash = hashSenha(senha, salt)
        UsuarioDAO(context).cadastrar(nome, email, hash, salt)
    }
}
