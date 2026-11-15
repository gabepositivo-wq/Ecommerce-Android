package com.example.novoecommerce.data.dal

import android.content.ContentValues
import android.content.Context
import com.example.novoecommerce.data.Endereco
import com.example.novoecommerce.data.local.AppDatabase

class EnderecoDAO(context: Context) {
    private val database = AppDatabase.getInstance(context)

    fun criar(endereco: Endereco): Long = database.writableDatabase.insertOrThrow(
        "enderecos",
        null,
        endereco.toContentValues()
    )

    fun listar(): List<Endereco> {
        val enderecos = mutableListOf<Endereco>()
        database.readableDatabase.query(
            "enderecos",
            arrayOf("id", "apelido", "cep", "rua", "bairro", "cidade", "estado"),
            null,
            null,
            null,
            null,
            "apelido COLLATE NOCASE"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                enderecos.add(
                    Endereco(
                        id = cursor.getInt(0),
                        apelido = cursor.getString(1),
                        cep = cursor.getString(2),
                        rua = cursor.getString(3),
                        bairro = cursor.getString(4),
                        cidade = cursor.getString(5),
                        estado = cursor.getString(6)
                    )
                )
            }
        }
        return enderecos
    }

    fun alterar(endereco: Endereco): Int =
        database.writableDatabase.update(
            "enderecos",
            endereco.toContentValues(),
            "id = ?",
            arrayOf(endereco.id.toString())
        )

    fun excluir(id: Int): Int =
        database.writableDatabase.delete("enderecos", "id = ?", arrayOf(id.toString()))

    private fun Endereco.toContentValues() = ContentValues().apply {
        put("apelido", apelido)
        put("cep", cep)
        put("rua", rua)
        put("bairro", bairro)
        put("cidade", cidade)
        put("estado", estado)
    }
}
