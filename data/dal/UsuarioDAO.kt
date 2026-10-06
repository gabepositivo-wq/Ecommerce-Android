package com.example.novoecommerce.data.dal

import android.content.ContentValues
import android.content.Context
import com.example.novoecommerce.data.local.AppDatabase

internal class SenhaSalva(val hash: ByteArray, val salt: ByteArray)

internal class UsuarioDAO(context: Context) {
    private val appContext = context.applicationContext

    fun cadastrar(nome: String, email: String, senha: ByteArray, salt: ByteArray) {
        AppDatabase(appContext).use { db ->
            val dados = ContentValues().apply {
                put("nome", nome)
                put("email", email)
                put("senha", senha)
                put("salt", salt)
            }
            db.writableDatabase.insertOrThrow("usuarios", null, dados)
        }
    }

    fun buscarSenha(email: String): SenhaSalva? {
        return AppDatabase(appContext).use { db ->
            db.readableDatabase.query(
                "usuarios",
                arrayOf("senha", "salt"),
                "email = ?",
                arrayOf(email),
                null, null, null
            ).use { cursor ->
                if (cursor.moveToFirst()) SenhaSalva(
                    cursor.getBlob(0),
                    cursor.getBlob(1)
                )
                else null
            }
        }
    }
}
