package com.example.novoecommerce.auth

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object AuthManager {
    private const val ITERACOES = 60_000

    fun criarSalt(): ByteArray = ByteArray(16).also { SecureRandom().nextBytes(it) }

    fun gerarHash(senha: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(senha.toCharArray(), salt, ITERACOES, 256)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                .generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    fun validarSenha(senha: String, salt: ByteArray, senhaDB: ByteArray): Boolean {
        val calculo = gerarHash(senha, salt)
        return try {
            MessageDigest.isEqual(senhaDB, calculo)
        } finally {
            calculo.fill(0)
        }
    }
}
