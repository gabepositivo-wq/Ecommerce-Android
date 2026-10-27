package com.example.novoecommerce

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.novoecommerce.auth.AuthViewModel
import com.example.novoecommerce.navigation.NovoEcommerceApp
import com.example.novoecommerce.ui.theme.NovoEcommerceTheme
import com.example.novoecommerce.viewmodel.CartViewModel
import com.example.novoecommerce.viewmodel.ProdutoViewModel

class MainActivity : ComponentActivity() {

    private val cartViewModel: CartViewModel by viewModels()
    private val produtoViewModel: ProdutoViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            NovoEcommerceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NovoEcommerceApp(cartViewModel, produtoViewModel, authViewModel)
                }
            }
        }
    }
}
