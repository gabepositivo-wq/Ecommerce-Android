package com.example.novoecommerce.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.novoecommerce.data.Product
import com.example.novoecommerce.data.dal.ProdutoDAO

class ProdutoViewModel : ViewModel() {
    private val _produtos = mutableStateListOf<Product>()
    val produtos: List<Product> get() = _produtos
    val categorias: List<String> get() = _produtos.map { it.category }.distinct().sorted()

    fun carregar(context: Context) {
        _produtos.clear()
        _produtos.addAll(ProdutoDAO(context).listar())
    }

    fun buscar(id: Int): Product? = _produtos.firstOrNull { it.id == id }

    fun porCategoria(categoria: String): List<Product> =
        _produtos.filter { it.category.equals(categoria, ignoreCase = true) }

    fun criar(context: Context, produto: Product) {
        validar(produto)
        ProdutoDAO(context).criar(produto.copy(id = 0))
        carregar(context)
    }

    fun alterar(context: Context, produto: Product) {
        validar(produto)
        if (ProdutoDAO(context).alterar(produto) == 0) {
            throw IllegalStateException("Produto não encontrado.")
        }
        carregar(context)
    }

    fun excluir(context: Context, id: Int) {
        if (ProdutoDAO(context).excluir(id) == 0) {
            throw IllegalStateException("Produto não encontrado.")
        }
        carregar(context)
    }

    private fun validar(produto: Product) {
        require(produto.name.isNotBlank()) { "Informe o nome do produto." }
        require(produto.category.isNotBlank()) { "Informe a categoria." }
        require(produto.price.isFinite() && produto.price >= 0.0) { "Informe um preço válido." }
    }
}
