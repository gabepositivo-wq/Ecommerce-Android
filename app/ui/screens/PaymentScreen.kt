package com.example.novoecommerce.ui.screens

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.novoecommerce.ui.components.AppHeader
import com.example.novoecommerce.ui.components.BottomPanel
import com.example.novoecommerce.ui.theme.Gray700
import com.example.novoecommerce.util.masked
import com.example.novoecommerce.util.toBrl
import com.example.novoecommerce.viewmodel.CartViewModel

@Composable
fun PaymentScreen(
    cart: CartViewModel,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    var editCard by remember { mutableStateOf(false) }
    var editAddress by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        AppHeader(title = "Pagamento", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            InfoCard(title = "Forma de pagamento", onEdit = { editCard = true }) {
                Text("💳  ${cart.cardNumber.masked()}", fontWeight = FontWeight.Bold)
            }

            InfoCard(title = "Local de entrega", onEdit = { editAddress = true }) {
                Text("Nome: ${cart.customerName}", fontWeight = FontWeight.Bold)
                Text("Rua: ${cart.street}", fontWeight = FontWeight.Bold)
                Text("Cep: ${cart.cep.masked(3)}", fontWeight = FontWeight.Bold)
            }
        }

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

    if (editCard) {
        EditDialog(
            title = "Forma de pagamento",
            labels = listOf("Número do cartão"),
            initial = listOf(cart.cardNumber),
            onDismiss = { editCard = false },
            onSave = {
                cart.cardNumber = it[0]
                editCard = false
            }
        )
    }

    if (editAddress) {
        EditDialog(
            title = "Local de entrega",
            labels = listOf("Nome", "Rua", "CEP"),
            initial = listOf(cart.customerName, cart.street, cart.cep),
            onDismiss = { editAddress = false },
            onSave = {
                cart.customerName = it[0]
                cart.street = it[1]
                cart.cep = it[2]
                editAddress = false
            }
        )
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

@Composable
private fun EditDialog(
    title: String,
    labels: List<String>,
    initial: List<String>,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit
) {
    val values = remember { mutableStateListOf<String>().apply { addAll(initial) } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                labels.forEachIndexed { index, label ->
                    OutlinedTextField(
                        value = values[index],
                        onValueChange = { values[index] = it },
                        label = { Text(label) },
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = { onSave(values.toList()) }) { Text("Salvar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
