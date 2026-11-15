package com.example.novoecommerce.data.dal

import android.content.ContentValues
import android.content.Context
import com.example.novoecommerce.data.Categoria
import com.example.novoecommerce.data.local.AppDatabase

class CategoriaDAO(context: Context) {
    private val database = AppDatabase.getInstance(context)

    fun criar(categoria: Categoria): Long = database.writableDatabase.insertOrThrow(
        "categorias",
        null,
        categoria.toContentValues()
    )

    fun listar(): List<Categoria> {
        val categorias = mutableListOf<Categoria>()
        database.readableDatabase.query(
            "categorias",
            arrayOf("id", "nome", "descricao"),
            null,
            null,
            null,
            null,
            "nome COLLATE NOCASE"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                categorias.add(
                    Categoria(
                        id = cursor.getInt(0),
                        nome = cursor.getString(1),
                        descricao = cursor.getString(2)
                    )
                )
            }
        }
        return categorias
    }

    fun alterar(categoria: Categoria): Int =
        database.writableDatabase.update(
            "categorias",
            categoria.toContentValues(),
            "id = ?",
            arrayOf(categoria.id.toString())
        )

    fun excluir(id: Int): Int =
        database.writableDatabase.delete("categorias", "id = ?", arrayOf(id.toString()))

    private fun Categoria.toContentValues() = ContentValues().apply {
        put("nome", nome)
        put("descricao", descricao)
    }
}
