package com.example.novoecommerce.ui.screens

import android.database.sqlite.SQLiteConstraintException
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.novoecommerce.auth.AuthViewModel

@Composable
fun LoginScreen(auth: AuthViewModel, onLoginSuccess: () -> Unit) {
    var abaSelecionada by rememberSaveable { mutableIntStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(64.dp))
        Text("NovoEcommerce", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Sua loja de eletrônicos", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(40.dp))
        TabRow(selectedTabIndex = abaSelecionada) {
            Tab(selected = abaSelecionada == 0, onClick = { abaSelecionada = 0 }, text = { Text("Login") })
            Tab(selected = abaSelecionada == 1, onClick = { abaSelecionada = 1 }, text = { Text("Cadastro") })
        }
        Spacer(Modifier.height(24.dp))
        if (abaSelecionada == 0) {
            FormularioLogin(auth, onLoginSuccess)
        } else {
            FormularioCadastro(auth) { abaSelecionada = 0 }
        }
    }
}

@Composable
private fun FormularioLogin(auth: AuthViewModel, onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var erro by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; erro = "" },
            label = { Text("E-mail") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it; erro = "" },
            label = { Text("Senha") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        if (erro.isNotEmpty()) Text(erro, color = MaterialTheme.colorScheme.error)
        Button(
            onClick = {
                if (email.isBlank() || senha.isBlank()) {
                    erro = "Informe e-mail e senha."
                } else if (auth.login(context, email.trim(), senha)) {
                    onLoginSuccess()
                } else {
                    erro = "E-mail ou senha incorretos."
                }
            },
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Entrar", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FormularioCadastro(auth: AuthViewModel, onCadastroSuccess: () -> Unit) {
    val context = LocalContext.current
    var nome by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var confirmarSenha by rememberSaveable { mutableStateOf("") }
    var erro by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedTextField(nome, { nome = it; erro = "" }, label = { Text("Nome") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            email,
            { email = it; erro = "" },
            label = { Text("E-mail") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            senha,
            { senha = it; erro = "" },
            label = { Text("Senha") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            confirmarSenha,
            { confirmarSenha = it; erro = "" },
            label = { Text("Confirmar senha") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        if (erro.isNotEmpty()) Text(erro, color = MaterialTheme.colorScheme.error)
        Button(
            onClick = {
                if (senha != confirmarSenha) {
                    erro = "As senhas não coincidem."
                } else {
                    try {
                        auth.cadastrar(context, nome.trim(), email.trim(), senha)
                        onCadastroSuccess()
                    } catch (exception: IllegalArgumentException) {
                        erro = exception.message ?: "Dados inválidos."
                    } catch (exception: SQLiteConstraintException) {
                        erro = "Este e-mail já está cadastrado."
                    }
                }
            },
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Cadastrar", fontWeight = FontWeight.Bold)
        }
    }
}
