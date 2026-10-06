package com.example.novoecommerce.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.novoecommerce.data.ProductRepository
import com.example.novoecommerce.ui.components.AppHeader
import com.example.novoecommerce.ui.theme.Orange
import com.example.novoecommerce.util.toBrl
import com.example.novoecommerce.viewmodel.CartViewModel

@Composable
fun ProductDetailScreen(
    productId: Int,
    cart: CartViewModel,
    onBack: () -> Unit,
    onCartClick: () -> Unit
) {
    val product = ProductRepository.byId(productId)
    val context = LocalContext.current
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        AppHeader(
            title = "Detalhes de Produto",
            onBack = onBack,
            onCartClick = onCartClick,
            cartCount = cart.itemCount
        )

        if (product == null) {
            Text("Produto não encontrado", modifier = Modifier.padding(24.dp))
            return@Column
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(product.emoji, fontSize = 110.sp)
            }

            Text(product.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Descrição:", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(product.price.toBrl(), fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                text = product.description,
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 24.sp
            )
            Text(
                text = if (expanded) "less" else "...more",
                color = Orange,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { expanded = !expanded }
            )
        }

        Button(
            onClick = {
                cart.add(product)
                Toast.makeText(context, "Adicionado ao carrinho", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .height(52.dp)
        ) {
            Text("Adicionar ao carrinho", fontWeight = FontWeight.Bold)
        }
    }
}
