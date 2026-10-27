package com.example.novoecommerce.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.novoecommerce.data.Product
import com.example.novoecommerce.ui.components.AppHeader
import com.example.novoecommerce.util.toBrl
import com.example.novoecommerce.viewmodel.ProdutoViewModel

@Composable
fun ProdutoCrudScreen(produtos: ProdutoViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var idEditando by rememberSaveable { mutableIntStateOf(-1) }
    var nome by rememberSaveable { mutableStateOf("") }
    var preco by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf("") }
    var emoji by rememberSaveable { mutableStateOf("📦") }
    var descricao by rememberSaveable { mutableStateOf("") }
    var erro by rememberSaveable { mutableStateOf("") }

    fun limparFormulario() {
        idEditando = -1
        nome = ""
        preco = ""
        categoria = ""
        emoji = "📦"
        descricao = ""
    }

    Column(Modifier.fillMaxSize()) {
        AppHeader(title = "Gerenciar produtos", onBack = onBack)
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(nome, { nome = it }, label = { Text("Nome") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    preco,
                    { preco = it },
                    label = { Text("Preço") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    categoria,
                    { categoria = it },
                    label = { Text("Categoria") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(emoji, { emoji = it }, label = { Text("Ícone") }, singleLine = true, modifier = Modifier.weight(1f))
                OutlinedTextField(descricao, { descricao = it }, label = { Text("Descrição") }, modifier = Modifier.weight(2f))
            }
            if (erro.isNotEmpty()) Text(erro, color = MaterialTheme.colorScheme.error)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val valor = preco.replace(',', '.').toDoubleOrNull()
                    if (valor == null) {
                        erro = "Informe um preço válido."
                    } else {
                        try {
                            val produto = Product(idEditando, nome.trim(), valor, categoria.trim(), emoji.trim(), descricao.trim())
                            if (idEditando < 0) produtos.criar(context, produto) else produtos.alterar(context, produto)
                            limparFormulario()
                            erro = ""
                        } catch (exception: IllegalArgumentException) {
                            erro = exception.message ?: "Confira os dados do produto."
                        } catch (exception: IllegalStateException) {
                            erro = exception.message ?: "Não foi possível salvar o produto."
                        }
                    }
                }) {
                    Text(if (idEditando < 0) "Adicionar" else "Salvar")
                }
                if (idEditando >= 0) TextButton(onClick = { limparFormulario(); erro = "" }) {
                    Text("Cancelar")
                }
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(produtos.produtos, key = { it.id }) { produto ->
                Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("${produto.emoji}  ${produto.name}", style = MaterialTheme.typography.titleMedium)
                        Text("${produto.category} · ${produto.price.toBrl()}")
                        Row {
                            TextButton(onClick = {
                                idEditando = produto.id
                                nome = produto.name
                                preco = produto.price.toString()
                                categoria = produto.category
                                emoji = produto.emoji
                                descricao = produto.description
                                erro = ""
                            }) { Text("Alterar") }
                            TextButton(onClick = {
                                try {
                                    produtos.excluir(context, produto.id)
                                    if (idEditando == produto.id) limparFormulario()
                                    erro = ""
                                } catch (exception: IllegalStateException) {
                                    erro = exception.message ?: "Não foi possível excluir o produto."
                                }
                            }) { Text("Excluir") }
                        }
                    }
                }
            }
        }
    }
}
