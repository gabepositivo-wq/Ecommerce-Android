package com.example.novoecommerce.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.novoecommerce.auth.AuthViewModel
import com.example.novoecommerce.ui.components.AppHeader
import com.example.novoecommerce.ui.components.Pill
import com.example.novoecommerce.ui.components.ProductCard
import com.example.novoecommerce.ui.components.listPadding
import com.example.novoecommerce.viewmodel.CartViewModel
import com.example.novoecommerce.viewmodel.ProdutoViewModel

@Composable
fun HomeScreen(
    cart: CartViewModel,
    produtos: ProdutoViewModel,
    auth: AuthViewModel,
    onManageProducts: () -> Unit,
    onManageUsers: () -> Unit,
    onManageCategories: () -> Unit,
    onManageAddresses: () -> Unit,
    onLogout: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    val products = produtos.produtos.filter { it.name.contains(query, ignoreCase = true) }

    Scaffold(
        topBar = {
            AppHeader(
                title = "Home",
                onCartClick = onCartClick,
                cartCount = cart.itemCount,
                query = query,
                onQueryChange = { query = it }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Olá, ${auth.usuarioAtual?.nome.orEmpty()}")
                    TextButton(onClick = onLogout) { Text("Sair") }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = onManageProducts, modifier = Modifier.weight(1f)) {
                        Text("Produtos")
                    }
                    Button(onClick = onManageUsers, modifier = Modifier.weight(1f)) {
                        Text("Usuários")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = onManageCategories, modifier = Modifier.weight(1f)) {
                        Text("Categorias")
                    }
                    Button(onClick = onManageAddresses, modifier = Modifier.weight(1f)) {
                        Text("Endereços")
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = listPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Categorias",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                items(produtos.categorias.chunked(3)) { linha ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        linha.forEach { category ->
                            Pill(
                                text = category,
                                modifier = Modifier.weight(1f),
                                onClick = { onCategoryClick(category) }
                            )
                        }
                        repeat(3 - linha.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
                item {
                    Text(
                        text = "Produtos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(products.chunked(2)) { linha ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        linha.forEach { product ->
                            Column(Modifier.weight(1f)) {
                                ProductCard(product = product, onClick = { onProductClick(product.id) })
                            }
                        }
                        if (linha.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
