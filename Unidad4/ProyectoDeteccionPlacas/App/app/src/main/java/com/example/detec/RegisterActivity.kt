package com.example.detec

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.detec.model.RegisterRequest
import com.example.detec.network.RetrofitClient
import com.example.detec.ui.theme.DeTECTheme
import kotlinx.coroutines.launch

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeTECTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    RegisterScreen()
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bkg_app),
            contentDescription = null,
            alpha = 0.85f,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Text(text = "deTec", fontSize = 48.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(30.dp))

            val componentShape = RoundedCornerShape(12.dp)

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                shape = componentShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.15f), // Estilo translúcido del arch 1
                    cursorColor = Color.White,
                    focusedLabelColor = Color(0xFF6200EE),
                    unfocusedLabelColor = Color.White
                ),
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
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
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
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
            Spacer(modifier = Modifier.height(25.dp))

            Button(
                onClick = {
                    if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        scope.launch {
                            try {
                                val request = RegisterRequest(username, email, password)
                                val response = RetrofitClient.apiService.register(request)

                                if (response.isSuccessful && response.body()?.usuario != null) {
                                    val user = response.body()?.usuario!!
                                    val session = SessionManager(context)
                                    session.saveUser(user.id, user.nombre, user.correo)
                                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                    onRegisterSuccess()
                                } else {
                                    //val errorJson = response.errorBody()?.string()
                                    Toast.makeText(context, "Error: de conexión, servidor iniciandose", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        Toast.makeText(context, "Llena todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE), contentColor = Color.White),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text(text = "Registrarse", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { onNavigateToLogin() }) {
                Text(text = "¿Ya tienes cuenta? Inicia sesión", color = Color.White)
            }
        }
    }
}