package com.example.novoecommerce.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.novoecommerce.data.Categoria
import com.example.novoecommerce.data.dal.CategoriaDAO

class CategoriaViewModel : ViewModel() {
    private val _categorias = mutableStateListOf<Categoria>()
    val categorias: List<Categoria> get() = _categorias

    fun carregar(context: Context) {
        _categorias.clear()
        _categorias.addAll(CategoriaDAO(context).listar())
    }

    fun criar(context: Context, categoria: Categoria) {
        validar(categoria)
        CategoriaDAO(context).criar(categoria.copy(id = 0))
        carregar(context)
    }

    fun alterar(context: Context, categoria: Categoria) {
        validar(categoria)
        if (CategoriaDAO(context).alterar(categoria) == 0) {
            throw IllegalStateException("Categoria não encontrada.")
        }
        carregar(context)
    }

    fun excluir(context: Context, id: Int) {
        if (CategoriaDAO(context).excluir(id) == 0) {
            throw IllegalStateException("Categoria não encontrada.")
        }
        carregar(context)
    }

    private fun validar(categoria: Categoria) {
        require(categoria.nome.isNotBlank()) { "Informe o nome da categoria." }
        require(categoria.descricao.isNotBlank()) { "Informe a descrição da categoria." }
    }
}
