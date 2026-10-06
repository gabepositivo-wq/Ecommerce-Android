package com.example.novoecommerce.data

/** Dados fixos (em memória) apenas para o trabalho escolar. */
object ProductRepository {

    val categories = listOf("Games", "Phones", "Mouse", "Keyboard", "Headset", "Monitor")

    val products = listOf(
        Product(1,  "Controle Gamer",    249.90,  "Games",    "🎮"),
        Product(2,  "Console Portátil",  1899.00, "Games",    "🕹️"),
        Product(3,  "Smartphone X",      1999.90, "Phones",   "📱"),
        Product(4,  "Smartphone Lite",   999.00,  "Phones",   "📱"),
        Product(5,  "Logitech Mouse",    129.90,  "Mouse",    "🖱️"),
        Product(6,  "Mouse Gamer RGB",   89.90,   "Mouse",    "🖱️"),
        Product(7,  "Logitech Keyboard", 100.00,  "Keyboard", "⌨️"),
        Product(8,  "Teclado Mecânico",  299.90,  "Keyboard", "⌨️"),
        Product(9,  "Headset Gamer",     199.90,  "Headset",  "🎧"),
        Product(10, "Fone Bluetooth",    149.90,  "Headset",  "🎧"),
        Product(11, "Monitor 24\"",      899.90,  "Monitor",  "🖥️"),
        Product(12, "Monitor Curvo 27\"",1499.90, "Monitor",  "🖥️")
    )

    fun byId(id: Int): Product? = products.firstOrNull { it.id == id }

    fun byCategory(category: String): List<Product> =
        products.filter { it.category.equals(category, ignoreCase = true) }
}
