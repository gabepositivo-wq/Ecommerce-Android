package com.example.novoecommerce.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.novoecommerce.data.ProductRepository
import java.io.File

internal class AppDatabase private constructor(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    File(context.noBackupFilesDir, "aula_sqlite.db").absolutePath,
    null,
    2
) {
    override fun onCreate(db: SQLiteDatabase) {
        criarTabelaUsuarios(db)
        criarTabelaProdutos(db)
        inserirProdutosIniciais(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            criarTabelaProdutos(db)
            inserirProdutosIniciais(db)
        }
    }

    private fun criarTabelaUsuarios(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE usuarios(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL CHECK(length(nome) BETWEEN 3 AND 100),
                email TEXT NOT NULL UNIQUE COLLATE NOCASE CHECK(length(email) BETWEEN 3 AND 254),
                senha BLOB NOT NULL,
                salt BLOB NOT NULL
            )
            """.trimIndent()
        )
    }

    private fun criarTabelaProdutos(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE produtos(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                preco REAL NOT NULL CHECK(preco >= 0),
                categoria TEXT NOT NULL,
                emoji TEXT NOT NULL,
                descricao TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    private fun inserirProdutosIniciais(db: SQLiteDatabase) {
        ProductRepository.products.forEach { product ->
            db.execSQL(
                """
                INSERT INTO produtos(id, nome, preco, categoria, emoji, descricao)
                VALUES(?, ?, ?, ?, ?, ?)
                """.trimIndent(),
                arrayOf(
                    product.id,
                    product.name,
                    product.price,
                    product.category,
                    product.emoji,
                    product.description
                )
            )
        }
    }

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: AppDatabase(context).also { instance = it }
            }
    }
}
