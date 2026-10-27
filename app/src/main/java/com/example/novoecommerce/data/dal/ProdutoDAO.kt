package com.example.novoecommerce.data.dal

import android.content.ContentValues
import android.content.Context
import com.example.novoecommerce.data.Product
import com.example.novoecommerce.data.local.AppDatabase

class ProdutoDAO(context: Context) {
    private val database = AppDatabase.getInstance(context)

    fun criar(produto: Product): Long = database.writableDatabase.insertOrThrow(
        "produtos",
        null,
        produto.toContentValues()
    )

    fun listar(): List<Product> {
        val produtos = mutableListOf<Product>()
        database.readableDatabase.query(
            "produtos",
            arrayOf("id", "nome", "preco", "categoria", "emoji", "descricao"),
            null,
            null,
            null,
            null,
            "nome COLLATE NOCASE"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                produtos.add(
                    Product(
                        id = cursor.getInt(0),
                        name = cursor.getString(1),
                        price = cursor.getDouble(2),
                        category = cursor.getString(3),
                        emoji = cursor.getString(4),
                        description = cursor.getString(5)
                    )
                )
            }
        }
        return produtos
    }

    fun buscar(id: Int): Product? =
        database.readableDatabase.query(
            "produtos",
            arrayOf("id", "nome", "preco", "categoria", "emoji", "descricao"),
            "id = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        ).use { cursor ->
            if (!cursor.moveToFirst()) null
            else Product(
                id = cursor.getInt(0),
                name = cursor.getString(1),
                price = cursor.getDouble(2),
                category = cursor.getString(3),
                emoji = cursor.getString(4),
                description = cursor.getString(5)
            )
        }

    fun alterar(produto: Product): Int =
        database.writableDatabase.update(
            "produtos",
            produto.toContentValues(),
            "id = ?",
            arrayOf(produto.id.toString())
        )

    fun excluir(id: Int): Int =
        database.writableDatabase.delete("produtos", "id = ?", arrayOf(id.toString()))

    private fun Product.toContentValues() = ContentValues().apply {
        put("nome", name)
        put("preco", price)
        put("categoria", category)
        put("emoji", emoji)
        put("descricao", description)
    }
}
