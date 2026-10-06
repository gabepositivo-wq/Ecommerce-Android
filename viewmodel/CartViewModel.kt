package com.example.novoecommerce.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.novoecommerce.data.CartItem
import com.example.novoecommerce.data.Product

class CartViewModel : ViewModel() {

    companion object {
        const val DELIVERY_FEE = 15.0
    }

    private val _items = mutableStateListOf<CartItem>()
    val items: List<CartItem> get() = _items

    val itemCount: Int get() = _items.sumOf { it.quantity }
    val total: Double get() = _items.sumOf { it.product.price * it.quantity }
    val deliveryFee: Double get() = if (_items.isEmpty()) 0.0 else DELIVERY_FEE

    // Dados da tela de pagamento
    var cardNumber by mutableStateOf("1234123412341234")
    var customerName by mutableStateOf("Cliente")
    var street by mutableStateOf("Rua das Piaba")
    var cep by mutableStateOf("80000000")

    fun add(product: Product) {
        val index = _items.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            _items[index] = _items[index].copy(quantity = _items[index].quantity + 1)
        } else {
            _items.add(CartItem(product, 1))
        }
    }

    fun changeQuantity(productId: Int, delta: Int) {
        val index = _items.indexOfFirst { it.product.id == productId }
        if (index < 0) return
        val newQuantity = _items[index].quantity + delta
        if (newQuantity <= 0) {
            _items.removeAt(index)
        } else {
            _items[index] = _items[index].copy(quantity = newQuantity)
        }
    }

    fun remove(productId: Int) {
        _items.removeAll { it.product.id == productId }
    }

    fun clear() = _items.clear()
}
