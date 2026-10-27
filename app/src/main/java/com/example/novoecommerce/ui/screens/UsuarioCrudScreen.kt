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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.novoecommerce.auth.AuthViewModel
import com.example.novoecommerce.data.Usuario
import com.example.novoecommerce.ui.components.AppHeader

@Composable
fun UsuarioCrudScreen(auth: AuthViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var usuarios by remember { mutableStateOf(emptyList<Usuario>()) }
    var idEditando by rememberSaveable { mutableIntStateOf(-1) }
    var nome by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var erro by rememberSaveable { mutableStateOf("") }

    fun carregarUsuarios() {
        usuarios = auth.listarUsuarios(context)
    }

    LaunchedEffect(auth) { carregarUsuarios() }

    fun limparFormulario() {
        idEditando = -1
        nome = ""
        email = ""
        senha = ""
    }

    Column(Modifier.fillMaxSize()) {
        AppHeader(title = "Gerenciar usuários", onBack = onBack)
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(nome, { nome = it }, label = { Text("Nome") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(email, { email = it }, label = { Text("E-mail") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            if (idEditando < 0) {
                OutlinedTextField(
                    senha,
                    { senha = it },
                    label = { Text("Senha") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (erro.isNotEmpty()) Text(erro, color = MaterialTheme.colorScheme.error)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    try {
                        if (idEditando < 0) {
                            auth.cadastrar(context, nome.trim(), email.trim(), senha)
                        } else {
                            auth.alterarUsuario(context, Usuario(idEditando, nome.trim(), email.trim()))
                        }
                        carregarUsuarios()
                        limparFormulario()
                        erro = ""
                    } catch (exception: IllegalArgumentException) {
                        erro = exception.message ?: "Confira os dados do usuário."
                    } catch (exception: SQLiteConstraintException) {
                        erro = "Este e-mail já está cadastrado."
                    } catch (exception: IllegalStateException) {
                        erro = exception.message ?: "Não foi possível salvar o usuário."
                    }
                }) {
                    Text(if (idEditando < 0) "Cadastrar" else "Salvar")
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
            items(usuarios, key = { it.id }) { usuario ->
                Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Text(usuario.nome, style = MaterialTheme.typography.titleMedium)
                        Text(usuario.email)
                        Row {
                            TextButton(onClick = {
                                idEditando = usuario.id
                                nome = usuario.nome
                                email = usuario.email
                                senha = ""
                                erro = ""
                            }) { Text("Alterar") }
                            TextButton(onClick = {
                                try {
                                    auth.excluirUsuario(context, usuario.id)
                                    carregarUsuarios()
                                    if (idEditando == usuario.id) limparFormulario()
                                    erro = ""
                                } catch (exception: IllegalArgumentException) {
                                    erro = exception.message ?: "Não foi possível excluir o usuário."
                                } catch (exception: IllegalStateException) {
                                    erro = exception.message ?: "Não foi possível excluir o usuário."
                                }
                            }) { Text("Excluir") }
                        }
                    }
                }
            }
        }
    }
}
