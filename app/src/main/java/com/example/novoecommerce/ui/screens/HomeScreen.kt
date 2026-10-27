package com.example.novoecommerce.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
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
    onLogout: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    val products = produtos.produtos.filter { it.name.contains(query, ignoreCase = true) }

    Column(Modifier.fillMaxSize()) {
        AppHeader(
            title = "Home",
            onCartClick = onCartClick,
            cartCount = cart.itemCount,
            query = query,
            onQueryChange = { query = it }
        )
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Olá, ${auth.usuarioAtual?.nome.orEmpty()}")
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
                TextButton(onClick = onLogout) { Text("Sair") }
            }
        }

        // Grade de 6 colunas: categorias ocupam 2 (3 por linha), produtos ocupam 3 (2 por linha).
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = listPadding(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "Categorias",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            items(produtos.categorias, span = { GridItemSpan(2) }) { category ->
                Pill(text = category, modifier = Modifier.fillMaxWidth(), onClick = { onCategoryClick(category) })
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "Products",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(products, key = { it.id }, span = { GridItemSpan(3) }) { product ->
                ProductCard(product = product, onClick = { onProductClick(product.id) })
            }
        }
    }
}
