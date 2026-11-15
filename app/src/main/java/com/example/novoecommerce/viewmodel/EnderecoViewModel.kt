package com.example.novoecommerce.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.novoecommerce.data.Endereco
import com.example.novoecommerce.data.dal.EnderecoDAO
import com.example.novoecommerce.data.remote.ViaCepApi

class EnderecoViewModel : ViewModel() {
    private val _enderecos = mutableStateListOf<Endereco>()
    val enderecos: List<Endereco> get() = _enderecos

    fun carregar(context: Context) {
        _enderecos.clear()
        _enderecos.addAll(EnderecoDAO(context).listar())
    }

    fun criar(context: Context, endereco: Endereco) {
        validar(endereco)
        EnderecoDAO(context).criar(endereco.copy(id = 0))
        carregar(context)
    }

    fun alterar(context: Context, endereco: Endereco) {
        validar(endereco)
        if (EnderecoDAO(context).alterar(endereco) == 0) {
            throw IllegalStateException("Endereço não encontrado.")
        }
        carregar(context)
    }

    fun excluir(context: Context, id: Int) {
        if (EnderecoDAO(context).excluir(id) == 0) {
            throw IllegalStateException("Endereço não encontrado.")
        }
        carregar(context)
    }

    suspend fun buscarCep(cep: String) =
        ViaCepApi.service.buscarCep(cep.filter { it.isDigit() })

    private fun validar(endereco: Endereco) {
        require(endereco.apelido.isNotBlank()) { "Informe um apelido para o endereço." }
        require(endereco.cep.filter { it.isDigit() }.length == 8) { "Informe um CEP válido com 8 dígitos." }
        require(endereco.rua.isNotBlank()) { "Informe a rua." }
        require(endereco.cidade.isNotBlank()) { "Informe a cidade." }
        require(endereco.estado.isNotBlank()) { "Informe o estado (UF)." }
    }
}
