package com.example.detec

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.detec.ui.theme.DeTECTheme
import com.example.detec.network.RetrofitClient
import com.example.detec.model.LoginRequest
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext


class LogInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeTECTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen()
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // Para mostrar carga

    val context = LocalContext.current
    val scope = rememberCoroutineScope() // Para lanzar la petición
    val componentShape = RoundedCornerShape(12.dp)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bkg_app),
            contentDescription = null,
            alpha = 0.85f,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay oscuro sutil para mejorar legibilidad
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.3f)
        ) {}

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Centrado vertical mejorado
        ) {

            Text(
                text = "deTec",
                fontSize = 48.sp, // Ligeramente más grande
                color = Color.White, // Blanco para resaltar sobre fondo
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                shape = componentShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.15f), // Fondo semitransparente
                    cursorColor = Color.White,
                    focusedLabelColor = Color(0xFF6200EE),
                    unfocusedLabelColor = Color.White
                ),
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                shape = componentShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.15f),
                    cursorColor = Color.White,
                    focusedLabelColor = Color(0xFF6200EE),
                    unfocusedLabelColor = Color.White
                ),
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            // BOTÓN ESTILO REPORT ACTIVITY
            Spacer(modifier = Modifier.height(32.dp))

            // BOTÓN MODIFICADO CON LÓGICA DE CONEXIÓN
            Button(
                onClick = {
                    // LÓGICA DE LOGIN
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        scope.launch {
                            isLoading = true
                            try {
                                // 1. Preparamos los datos con los nombres correctos
                                val request = LoginRequest(email, password)

                                // 2. Hacemos la llamada al servidor
                                val response = RetrofitClient.apiService.login(request)

                                if (response.isSuccessful && response.body()?.usuario != null) {
                                    val usuario = response.body()?.usuario!!
                                    val session = SessionManager(context)
                                    session.saveUser(usuario.id, usuario.nombre, usuario.correo)
                                    onLoginSuccess()
                                } else {
                                    // CORRECCIÓN: Leer errorBody()
                                    val errorBodyStr = response.errorBody()?.string()
                                    Toast.makeText(context, "Error servidor: $errorBodyStr", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                                e.printStackTrace()
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        Toast.makeText(context, "Llena todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isLoading, // Deshabilitar si está cargando
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE),
                    contentColor = Color.White
                ),
                shape = componentShape,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "INICIAR SESIÓN", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = { onNavigateToRegister() }) {
                Text(
                    text = "¿No tienes cuenta? Regístrate aquí",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Footer pegado al fondo
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "Derechos reservados @deTec",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    DeTECTheme {
        LoginScreen()
    }
}