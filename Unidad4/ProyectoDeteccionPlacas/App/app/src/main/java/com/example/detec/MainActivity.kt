package com.example.detec

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.detec.ui.theme.DeTECTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        despertarServidor()

        setContent {
            DeTECTheme {
                AppNavigation()
            }
        }
    }

    private fun despertarServidor() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d("API_WAKEUP", "Intentando despertar al servidor...")

                val url = "https://tu-espacio-usuario.hf.space/"

                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .build()

                client.newCall(request).execute()

                Log.d("API_WAKEUP", "Servidor contactado exitosamente (Ping enviado)")
            } catch (e: Exception) {
                Log.e("API_WAKEUP", "Error al intentar despertar: ${e.message}")
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToProfile = {
                    navController.navigate(Routes.USER_PROFILE)
                },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                } ,
                onReport = {
                    navController.navigate(Routes.REPORT)
                }
            )
        }

        composable(Routes.REPORT) {
            ReportScreen(
                onReport = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }


        composable(Routes.USER_PROFILE) {
            UserProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}