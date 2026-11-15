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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.novoecommerce.data.Endereco
import com.example.novoecommerce.ui.components.AppHeader
import com.example.novoecommerce.viewmodel.EnderecoViewModel
import kotlinx.coroutines.launch

@Composable
fun EnderecoCrudScreen(enderecos: EnderecoViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var idEditando by rememberSaveable { mutableIntStateOf(-1) }
    var apelido by rememberSaveable { mutableStateOf("") }
    var cep by rememberSaveable { mutableStateOf("") }
    var rua by rememberSaveable { mutableStateOf("") }
    var bairro by rememberSaveable { mutableStateOf("") }
    var cidade by rememberSaveable { mutableStateOf("") }
    var estado by rememberSaveable { mutableStateOf("") }
    var erro by rememberSaveable { mutableStateOf("") }
    var buscandoCep by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        enderecos.carregar(context)
    }

    fun limparFormulario() {
        idEditando = -1
        apelido = ""
        cep = ""
        rua = ""
        bairro = ""
        cidade = ""
        estado = ""
    }

    Scaffold(
        topBar = { AppHeader(title = "Endereços de entrega", onBack = onBack) }
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
                    value = apelido,
                    onValueChange = { apelido = it },
                    label = { Text("Apelido (ex: Casa, Trabalho)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = cep,
                        onValueChange = { cep = it },
                        label = { Text("CEP") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(
                        enabled = !buscandoCep,
                        onClick = {
                            val cepLimpo = cep.filter { it.isDigit() }
                            if (cepLimpo.length != 8) {
                                erro = "Informe um CEP com 8 dígitos para buscar."
                                return@OutlinedButton
                            }
                            buscandoCep = true
                            erro = ""
                            scope.launch {
                                try {
                                    val resp = enderecos.buscarCep(cepLimpo)
                                    if (resp.erro == true) {
                                        erro = "CEP não encontrado."
                                    } else {
                                        rua = resp.logradouro.orEmpty()
                                        bairro = resp.bairro.orEmpty()
                                        cidade = resp.localidade.orEmpty()
                                        estado = resp.uf.orEmpty()
                                    }
                                } catch (e: Exception) {
                                    erro = "Erro ao consultar ViaCEP: ${e.localizedMessage ?: "verifique a internet"}"
                                } finally {
                                    buscandoCep = false
                                }
                            }
                        }
                    ) {
                        Text(if (buscandoCep) "Buscando..." else "Buscar CEP")
                    }
                }
                OutlinedTextField(
                    value = rua,
                    onValueChange = { rua = it },
                    label = { Text("Rua") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = bairro,
                        onValueChange = { bairro = it },
                        label = { Text("Bairro") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = cidade,
                        onValueChange = { cidade = it },
                        label = { Text("Cidade") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = estado,
                        onValueChange = { estado = it },
                        label = { Text("UF") },
                        singleLine = true,
                        modifier = Modifier.weight(0.5f)
                    )
                }
                if (erro.isNotEmpty()) Text(erro, color = MaterialTheme.colorScheme.error)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        try {
                            val endereco = Endereco(
                                id = idEditando,
                                apelido = apelido.trim(),
                                cep = cep.filter { it.isDigit() },
                                rua = rua.trim(),
                                bairro = bairro.trim(),
                                cidade = cidade.trim(),
                                estado = estado.trim().uppercase()
                            )
                            if (idEditando < 0) enderecos.criar(context, endereco)
                            else enderecos.alterar(context, endereco)
                            limparFormulario()
                            erro = ""
                        } catch (e: IllegalArgumentException) {
                            erro = e.message ?: "Confira os dados do endereço."
                        } catch (e: IllegalStateException) {
                            erro = e.message ?: "Não foi possível salvar o endereço."
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
                items(enderecos.enderecos, key = { it.id }) { endereco ->
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
                            Text(endereco.apelido, style = MaterialTheme.typography.titleMedium)
                            Text("${endereco.rua} - ${endereco.bairro}")
                            Text("${endereco.cidade}/${endereco.estado} · CEP ${endereco.cep}")
                            Row {
                                TextButton(onClick = {
                                    idEditando = endereco.id
                                    apelido = endereco.apelido
                                    cep = endereco.cep
                                    rua = endereco.rua
                                    bairro = endereco.bairro
                                    cidade = endereco.cidade
                                    estado = endereco.estado
                                    erro = ""
                                }) { Text("Alterar") }
                                TextButton(onClick = {
                                    try {
                                        enderecos.excluir(context, endereco.id)
                                        if (idEditando == endereco.id) limparFormulario()
                                        erro = ""
                                    } catch (e: IllegalStateException) {
                                        erro = e.message ?: "Não foi possível excluir o endereço."
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
