package com.example.novoecommerce.data.dal

import android.content.ContentValues
import android.content.Context
import com.example.novoecommerce.data.Usuario
import com.example.novoecommerce.data.local.AppDatabase

internal class SenhaSalva(val hash: ByteArray, val salt: ByteArray)

class UsuarioDAO(context: Context) {
    private val database = AppDatabase.getInstance(context)

    fun cadastrar(nome: String, email: String, senha: ByteArray, salt: ByteArray) {
        val valores = ContentValues().apply {
            put("nome", nome)
            put("email", email)
            put("senha", senha)
            put("salt", salt)
        }
        database.writableDatabase.insertOrThrow("usuarios", null, valores)
    }

    fun listar(): List<Usuario> {
        val usuarios = mutableListOf<Usuario>()
        database.readableDatabase.query(
            "usuarios",
            arrayOf("id", "nome", "email"),
            null,
            null,
            null,
            null,
            "nome COLLATE NOCASE"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                usuarios.add(Usuario(cursor.getInt(0), cursor.getString(1), cursor.getString(2)))
            }
        }
        return usuarios
    }

    fun buscarPorId(id: Int): Usuario? =
        database.readableDatabase.query(
            "usuarios",
            arrayOf("id", "nome", "email"),
            "id = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                Usuario(cursor.getInt(0), cursor.getString(1), cursor.getString(2))
            } else {
                null
            }
        }

    fun alterar(usuario: Usuario): Int {
        val valores = ContentValues().apply {
            put("nome", usuario.nome)
            put("email", usuario.email)
        }
        return database.writableDatabase.update(
            "usuarios",
            valores,
            "id = ?",
            arrayOf(usuario.id.toString())
        )
    }

    fun excluir(id: Int): Int =
        database.writableDatabase.delete("usuarios", "id = ?", arrayOf(id.toString()))

    internal fun buscarSenha(email: String): SenhaSalva? =
        database.readableDatabase.query(
            "usuarios",
            arrayOf("senha", "salt"),
            "email = ?",
            arrayOf(email),
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) SenhaSalva(cursor.getBlob(0), cursor.getBlob(1)) else null
        }

    fun buscarIdPorEmail(email: String): Int? =
        database.readableDatabase.query(
            "usuarios",
            arrayOf("id"),
            "email = ?",
            arrayOf(email),
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getInt(0) else null
        }
}
