package com.example.detec

import android.os.Bundle
import android.util.Log // Importar Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope // Importar lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.detec.ui.theme.DeTECTheme
import kotlinx.coroutines.Dispatchers // Importar Dispatchers
import kotlinx.coroutines.launch     // Importar launch
import okhttp3.OkHttpClient          // Asumiendo que usas OkHttp
import okhttp3.Request               // Asumiendo que usas OkHttp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. LLAMADA AL DESPERTADOR
        // Se ejecuta apenas abre la app, en segundo plano.
        despertarServidor() // <--- AGREGAR ESTA LÍNEA

        setContent {
            DeTECTheme {
                AppNavigation()
            }
        }
    }

    // 2. FUNCIÓN PARA DESPERTAR (Pégala dentro de la clase MainActivity, antes o después de onCreate)
    private fun despertarServidor() {
        // Usamos lifecycleScope para lanzar una corrutina que muere si cierran la app
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d("API_WAKEUP", "Intentando despertar al servidor...")

                // TU URL DE HUGGING FACE AQUÍ
                // (No importa si da error 404 o 500, lo importante es que llegue la petición)
                val url = "https://tu-espacio-usuario.hf.space/"

                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .build()

                // Ejecutamos la llamada (no necesitamos leer la respuesta)
                client.newCall(request).execute()

                Log.d("API_WAKEUP", "Servidor contactado exitosamente (Ping enviado)")
            } catch (e: Exception) {
                // Si falla por internet o timeout, no pasa nada, es silencioso
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
                // BORRAMOS onTakePhoto y onSelectFromGallery PORQUE YA NO SE NECESITAN AQUÍ
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