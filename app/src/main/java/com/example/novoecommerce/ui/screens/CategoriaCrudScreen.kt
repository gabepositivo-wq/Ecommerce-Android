package com.example.novoecommerce.ui.screens

import android.database.sqlite.SQLiteConstraintException
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.novoecommerce.data.Categoria
import com.example.novoecommerce.ui.components.AppHeader
import com.example.novoecommerce.viewmodel.CategoriaViewModel

@Composable
fun CategoriaCrudScreen(categorias: CategoriaViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var idEditando by rememberSaveable { mutableIntStateOf(-1) }
    var nome by rememberSaveable { mutableStateOf("") }
    var descricao by rememberSaveable { mutableStateOf("") }
    var erro by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        categorias.carregar(context)
    }

    fun limparFormulario() {
        idEditando = -1
        nome = ""
        descricao = ""
    }

    Scaffold(
        topBar = { AppHeader(title = "Gerenciar categorias", onBack = onBack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome da categoria") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (erro.isNotEmpty()) Text(erro, color = MaterialTheme.colorScheme.error)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        try {
                            val categoria = Categoria(idEditando, nome.trim(), descricao.trim())
                            if (idEditando < 0) categorias.criar(context, categoria)
                            else categorias.alterar(context, categoria)
                            limparFormulario()
                            erro = ""
                        } catch (e: IllegalArgumentException) {
                            erro = e.message ?: "Confira os dados da categoria."
                        } catch (e: SQLiteConstraintException) {
                            erro = "Já existe uma categoria com este nome."
                        } catch (e: IllegalStateException) {
                            erro = e.message ?: "Não foi possível salvar a categoria."
                        }
                    }) {
                        Text(if (idEditando < 0) "Adicionar" else "Salvar")
                    }
                    if (idEditando >= 0) {
                        TextButton(onClick = { limparFormulario(); erro = "" }) {
                            Text("Cancelar")
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categorias.categorias, key = { it.id }) { categoria ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text(categoria.nome, style = MaterialTheme.typography.titleMedium)
                            Text(categoria.descricao)
                            Row {
                                TextButton(onClick = {
                                    idEditando = categoria.id
                                    nome = categoria.nome
                                    descricao = categoria.descricao
                                    erro = ""
                                }) { Text("Alterar") }
                                TextButton(onClick = {
                                    try {
                                        categorias.excluir(context, categoria.id)
                                        if (idEditando == categoria.id) limparFormulario()
                                        erro = ""
                                    } catch (e: IllegalStateException) {
                                        erro = e.message ?: "Não foi possível excluir a categoria."
                                    }
                                }) { Text("Excluir") }
                            }
                        }
                    }
                }
            }
        }
    }
}
