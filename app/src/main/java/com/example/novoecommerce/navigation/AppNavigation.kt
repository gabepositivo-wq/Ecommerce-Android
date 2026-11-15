package com.example.novoecommerce.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.novoecommerce.auth.AuthViewModel
import com.example.novoecommerce.ui.screens.CartScreen
import com.example.novoecommerce.ui.screens.CategoriaCrudScreen
import com.example.novoecommerce.ui.screens.CategoryScreen
import com.example.novoecommerce.ui.screens.EnderecoCrudScreen
import com.example.novoecommerce.ui.screens.HomeScreen
import com.example.novoecommerce.ui.screens.LoginScreen
import com.example.novoecommerce.ui.screens.PaymentScreen
import com.example.novoecommerce.ui.screens.ProductDetailScreen
import com.example.novoecommerce.ui.screens.ProdutoCrudScreen
import com.example.novoecommerce.ui.screens.UsuarioCrudScreen
import com.example.novoecommerce.viewmodel.CartViewModel
import com.example.novoecommerce.viewmodel.CategoriaViewModel
import com.example.novoecommerce.viewmodel.EnderecoViewModel
import com.example.novoecommerce.viewmodel.ProdutoViewModel
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

@Serializable
data object HomeRoute

@Serializable
data class CategoryRoute(val name: String)

@Serializable
data class ProductRoute(val id: Int)

@Serializable
data object CartRoute

@Serializable
data object PaymentRoute

@Serializable
data object ProdutosRoute

@Serializable
data object UsuariosRoute

@Serializable
data object CategoriasRoute

@Serializable
data object EnderecosRoute

@Composable
fun NovoEcommerceApp(
    cart: CartViewModel,
    produtos: ProdutoViewModel,
    auth: AuthViewModel,
    categorias: CategoriaViewModel,
    enderecos: EnderecoViewModel
) {
    val nav = rememberNavController()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        produtos.carregar(context)
        if (auth.restaurarSessao(context)) {
            nav.navigate(HomeRoute) {
                popUpTo(LoginRoute) { inclusive = true }
            }
        }
    }

    NavHost(navController = nav, startDestination = LoginRoute) {

        composable<LoginRoute> {
            LoginScreen(
                auth = auth,
                onLoginSuccess = {
                    nav.navigate(HomeRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composable<HomeRoute> {
            HomeScreen(
                cart = cart,
                produtos = produtos,
                auth = auth,
                onManageProducts = { nav.navigate(ProdutosRoute) },
                onManageUsers = { nav.navigate(UsuariosRoute) },
                onManageCategories = { nav.navigate(CategoriasRoute) },
                onManageAddresses = { nav.navigate(EnderecosRoute) },
                onLogout = {
                    auth.encerrarSessao(context)
                    nav.navigate(LoginRoute) {
                        popUpTo(HomeRoute) { inclusive = true }
                    }
                },
                onCategoryClick = { nav.navigate(CategoryRoute(it)) },
                onProductClick = { nav.navigate(ProductRoute(it)) },
                onCartClick = { nav.navigate(CartRoute) }
            )
        }

        composable<ProdutosRoute> {
            ProdutoCrudScreen(produtos = produtos, onBack = { nav.popBackStack() })
        }

        composable<UsuariosRoute> {
            UsuarioCrudScreen(auth = auth, onBack = { nav.popBackStack() })
        }

        composable<CategoriasRoute> {
            CategoriaCrudScreen(categorias = categorias, onBack = { nav.popBackStack() })
        }

        composable<EnderecosRoute> {
            EnderecoCrudScreen(enderecos = enderecos, onBack = { nav.popBackStack() })
        }

        composable<CategoryRoute> { entry ->
            val route = entry.toRoute<CategoryRoute>()
            CategoryScreen(
                category = route.name,
                produtos = produtos,
                cart = cart,
                onBack = { nav.popBackStack() },
                onProductClick = { nav.navigate(ProductRoute(it)) },
                onCartClick = { nav.navigate(CartRoute) }
            )
        }

        composable<ProductRoute> { entry ->
            val route = entry.toRoute<ProductRoute>()
            ProductDetailScreen(
                productId = route.id,
                produtos = produtos,
                cart = cart,
                onBack = { nav.popBackStack() },
                onCartClick = { nav.navigate(CartRoute) }
            )
        }

        composable<CartRoute> {
            CartScreen(
                cart = cart,
                onBack = { nav.popBackStack() },
                onCheckout = { nav.navigate(PaymentRoute) }
            )
        }

        composable<PaymentRoute> {
            PaymentScreen(
                cart = cart,
                onBack = { nav.popBackStack() },
                onFinish = {
                    cart.clear()
                    Toast.makeText(context, "Pedido realizado com sucesso!", Toast.LENGTH_LONG).show()
                    nav.popBackStack(HomeRoute, inclusive = false)
                }
            )
        }
    }
}
