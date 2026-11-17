package com.example.novoecommerce.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.novoecommerce.ui.components.AppHeader
import com.example.novoecommerce.ui.components.BottomPanel
import com.example.novoecommerce.ui.theme.Gray700
import com.example.novoecommerce.util.LocationHelper
import com.example.novoecommerce.util.masked
import com.example.novoecommerce.util.toBrl
import com.example.novoecommerce.viewmodel.CartViewModel

@Composable
fun PaymentScreen(
    cart: CartViewModel,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    var editCard by remember { mutableStateOf(false) }
    var editAddress by remember { mutableStateOf(false) }
    var buscandoLocalizacao by remember { mutableStateOf(false) }
    var erroLocalizacao by remember { mutableStateOf("") }

    fun carregarLocalizacao() {
        buscandoLocalizacao = true
        erroLocalizacao = ""
        LocationHelper.obterLocalizacaoEntrega(
            context = context,
            onSuccess = { resultado ->
                cart.street = resultado.street
                if (resultado.cep.isNotBlank()) {
                    cart.cep = resultado.cep
                }
                cart.deliveryLocationText = resultado.summary
                buscandoLocalizacao = false
            },
            onError = { mensagem ->
                erroLocalizacao = mensagem
                buscandoLocalizacao = false
            }
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedida ->
        if (concedida) {
            carregarLocalizacao()
        } else {
            erroLocalizacao = "Permissão de localização negada."
        }
    }

    Scaffold(
        topBar = { AppHeader(title = "Pagamento", onBack = onBack) },
        bottomBar = {
            BottomPanel {
                Text("Total: ${cart.total.toBrl()}", fontWeight = FontWeight.Bold)
                Text("Delivery: ${cart.deliveryFee.toBrl()}", fontWeight = FontWeight.Bold)
                Text(
                    text = "Total a pagar: ${(cart.total + cart.deliveryFee).toBrl()}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onFinish,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Finalizar pedido", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            InfoCard(title = "Forma de pagamento", onEdit = { editCard = !editCard }) {
                if (editCard) {
                    var cardInput by remember(cart.cardNumber) { mutableStateOf(cart.cardNumber) }
                    OutlinedTextField(
                        value = cardInput,
                        onValueChange = { cardInput = it },
                        label = { Text("Número do cartão") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            cart.cardNumber = cardInput
                            editCard = false
                        }) { Text("Salvar") }
                        TextButton(onClick = { editCard = false }) { Text("Cancelar") }
                    }
                } else {
                    Text("💳  ${cart.cardNumber.masked()}", fontWeight = FontWeight.Bold)
                }
            }

            InfoCard(title = "Local de entrega", onEdit = { editAddress = !editAddress }) {
                if (editAddress) {
                    var nameInput by remember(cart.customerName) { mutableStateOf(cart.customerName) }
                    var streetInput by remember(cart.street) { mutableStateOf(cart.street) }
                    var cepInput by remember(cart.cep) { mutableStateOf(cart.cep) }
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Nome") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = streetInput,
                        onValueChange = { streetInput = it },
                        label = { Text("Rua") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = cepInput,
                        onValueChange = { cepInput = it },
                        label = { Text("CEP") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            cart.customerName = nameInput
                            cart.street = streetInput
                            cart.cep = cepInput
                            editAddress = false
                        }) { Text("Salvar") }
                        TextButton(onClick = { editAddress = false }) { Text("Cancelar") }
                    }
                } else {
                    Text("Nome: ${cart.customerName}", fontWeight = FontWeight.Bold)
                    Text("Rua: ${cart.street}", fontWeight = FontWeight.Bold)
                    Text("Cep: ${cart.cep.masked(3)}", fontWeight = FontWeight.Bold)
                }
                if (cart.deliveryLocationText.isNotBlank()) {
                    Text(
                        text = "📍 ${cart.deliveryLocationText}",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                OutlinedButton(
                    enabled = !buscandoLocalizacao,
                    onClick = {
                        val granted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                        if (granted) {
                            carregarLocalizacao()
                        } else {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                    Text(
                        text = if (buscandoLocalizacao) " Obtendo localização..." else " Usar minha localização para entrega"
                    )
                }
                if (erroLocalizacao.isNotBlank()) {
                    Text(erroLocalizacao, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    onEdit: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Gray700)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("edit", fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onEdit))
        }
        content()
    }
}
