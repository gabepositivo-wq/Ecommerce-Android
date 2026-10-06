package com.example.novoecommerce.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.novoecommerce.data.CartItem
import com.example.novoecommerce.ui.components.AppHeader
import com.example.novoecommerce.ui.components.BottomPanel
import com.example.novoecommerce.ui.theme.Gray300
import com.example.novoecommerce.ui.theme.Gray700
import com.example.novoecommerce.util.toBrl
import com.example.novoecommerce.viewmodel.CartViewModel

@Composable
fun CartScreen(
    cart: CartViewModel,
    onBack: () -> Unit,
    onCheckout: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        AppHeader(title = "Cart", onBack = onBack)

        Text(
            text = "Showing ${cart.itemCount} products",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp)
        )

        if (cart.items.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Seu carrinho está vazio", color = Gray300)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(cart.items, key = { it.product.id }) { item ->
                    CartRow(
                        item = item,
                        onMinus = { cart.changeQuantity(item.product.id, -1) },
                        onPlus = { cart.changeQuantity(item.product.id, +1) },
                        onRemove = { cart.remove(item.product.id) }
                    )
                }
            }
        }

        BottomPanel {
            Text("Total: ${cart.total.toBrl()}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = onCheckout,
                enabled = cart.items.isNotEmpty(),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Prosseguir para pagamento", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CartRow(
    item: CartItem,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    onRemove: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Gray700),
            contentAlignment = Alignment.Center
        ) {
            Text(item.product.emoji, fontSize = 40.sp)
        }

        Spacer(Modifier.width(16.dp))

        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Nome: ${item.product.name}", fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Quantidade: ", fontWeight = FontWeight.Bold)
                IconButton(onClick = onMinus, modifier = Modifier.size(32.dp)) {
                    Text("−", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Text("${item.quantity}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                IconButton(onClick = onPlus, modifier = Modifier.size(32.dp)) {
                    Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text("Preço: ${(item.product.price * item.quantity).toBrl()}", fontWeight = FontWeight.Bold)
        }

        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Remover")
        }
    }
}
