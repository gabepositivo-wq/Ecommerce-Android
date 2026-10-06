package com.example.novoecommerce.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.novoecommerce.data.ProductRepository
import com.example.novoecommerce.ui.components.AppHeader
import com.example.novoecommerce.ui.components.Pill
import com.example.novoecommerce.ui.components.ProductCard
import com.example.novoecommerce.ui.components.listPadding
import com.example.novoecommerce.viewmodel.CartViewModel

@Composable
fun HomeScreen(
    cart: CartViewModel,
    onCategoryClick: (String) -> Unit,
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    val products = ProductRepository.products.filter { it.name.contains(query, ignoreCase = true) }

    Column(Modifier.fillMaxSize()) {
        AppHeader(
            title = "Home",
            onCartClick = onCartClick,
            cartCount = cart.itemCount,
            query = query,
            onQueryChange = { query = it }
        )

        // Grade de 6 colunas: categorias ocupam 2 (3 por linha), produtos ocupam 3 (2 por linha).
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier.fillMaxSize(),
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
            items(ProductRepository.categories, span = { GridItemSpan(2) }) { category ->
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
