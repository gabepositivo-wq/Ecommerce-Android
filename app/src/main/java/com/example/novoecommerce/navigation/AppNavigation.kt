package com.example.novoecommerce.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.novoecommerce.ui.screens.CartScreen
import com.example.novoecommerce.ui.screens.CategoryScreen
import com.example.novoecommerce.ui.screens.HomeScreen
import com.example.novoecommerce.ui.screens.PaymentScreen
import com.example.novoecommerce.ui.screens.ProductDetailScreen
import com.example.novoecommerce.viewmodel.CartViewModel

object Routes {
    const val HOME = "home"
    const val CATEGORY = "category/{name}"
    const val PRODUCT = "product/{id}"
    const val CART = "cart"
    const val PAYMENT = "payment"

    fun category(name: String) = "category/$name"
    fun product(id: Int) = "product/$id"
}

@Composable
fun NovoEcommerceApp(cart: CartViewModel) {
    val nav = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = nav, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                cart = cart,
                onCategoryClick = { nav.navigate(Routes.category(it)) },
                onProductClick = { nav.navigate(Routes.product(it)) },
                onCartClick = { nav.navigate(Routes.CART) }
            )
        }

        composable(
            route = Routes.CATEGORY,
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { entry ->
            CategoryScreen(
                category = entry.arguments?.getString("name").orEmpty(),
                cart = cart,
                onBack = { nav.popBackStack() },
                onProductClick = { nav.navigate(Routes.product(it)) },
                onCartClick = { nav.navigate(Routes.CART) }
            )
        }

        composable(
            route = Routes.PRODUCT,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            ProductDetailScreen(
                productId = entry.arguments?.getInt("id") ?: -1,
                cart = cart,
                onBack = { nav.popBackStack() },
                onCartClick = { nav.navigate(Routes.CART) }
            )
        }

        composable(Routes.CART) {
            CartScreen(
                cart = cart,
                onBack = { nav.popBackStack() },
                onCheckout = { nav.navigate(Routes.PAYMENT) }
            )
        }

        composable(Routes.PAYMENT) {
            PaymentScreen(
                cart = cart,
                onBack = { nav.popBackStack() },
                onFinish = {
                    cart.clear()
                    Toast.makeText(context, "Pedido realizado com sucesso!", Toast.LENGTH_LONG).show()
                    nav.popBackStack(Routes.HOME, inclusive = false)
                }
            )
        }
    }
}
