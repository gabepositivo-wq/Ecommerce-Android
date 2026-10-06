package com.example.novoecommerce.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.novoecommerce.data.ProductRepository
import com.example.novoecommerce.ui.components.AppHeader
import com.example.novoecommerce.ui.components.Pill
import com.example.novoecommerce.ui.components.ProductCard
import com.example.novoecommerce.ui.components.listPadding
import com.example.novoecommerce.viewmodel.CartViewModel

private enum class SortOption(val label: String) {
    NAME("Nome (A-Z)"),
    PRICE_ASC("Menor preço"),
    PRICE_DESC("Maior preço")
}

@Composable
fun CategoryScreen(
    category: String,
    cart: CartViewModel,
    onBack: () -> Unit,
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var sort by rememberSaveable { mutableStateOf(SortOption.NAME) }
    var menuOpen by rememberSaveable { mutableStateOf(false) }

    val filtered = ProductRepository.byCategory(category)
        .filter { it.name.contains(query, ignoreCase = true) }
    val products = when (sort) {
        SortOption.NAME -> filtered.sortedBy { it.name }
        SortOption.PRICE_ASC -> filtered.sortedBy { it.price }
        SortOption.PRICE_DESC -> filtered.sortedByDescending { it.price }
    }

    Column(Modifier.fillMaxSize()) {
        AppHeader(
            title = category,
            onBack = onBack,
            onCartClick = onCartClick,
            cartCount = cart.itemCount,
            query = query,
            onQueryChange = { query = it }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Showing ${products.size} products", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Box {
                Pill(text = "Sort By", onClick = { menuOpen = true })
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    SortOption.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                sort = option
                                menuOpen = false
                            }
                        )
                    }
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = listPadding(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products, key = { it.id }) { product ->
                ProductCard(product = product, onClick = { onProductClick(product.id) })
            }
        }
    }
}
