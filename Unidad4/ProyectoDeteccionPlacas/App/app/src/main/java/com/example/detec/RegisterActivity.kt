package com.example.detec

import android.os.Bundle
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.detec.ui.theme.DeTECTheme

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeTECTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegisterScreen()
                }
            }
        }
    }
}

@Composable
fun RegisterScreen() {
    // Variables de estado
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Estado para el scroll por si el teclado tapa los campos
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // --- CAPA 1: FONDO  ---
        Image(
            painter = painterResource(id = R.drawable.bkg_app),
            contentDescription = null,
            alpha = 0.85f,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // --- CAPA 2: CONTENIDO ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Título
            Text(
                text = "deTec",
                fontSize = 40.sp,
                color = Color(0xFF6200EE),
                fontWeight = Bold
            )

            Spacer(modifier = Modifier.height(30.dp))

            // CAMPO 1: USUARIO
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.White,
                    focusedTextColor = Color.White, // Ojo: revisa si se lee bien con fondo gris
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                ),
                label = { Text("Usuario") },
                placeholder = { Text("Ej: alanquinbel98...") }, // [cite: 9]
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(15.dp))

            // CAMPO 2: CORREO
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                ),
                label = { Text("Correo") },
                placeholder = { Text("Ej: alanquin95@gmai.com...") }, // [cite: 11]
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(15.dp))

            // CAMPO 3: CONTRASEÑA
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                ),
                label = { Text("Contraseña") },
                placeholder = { Text("Ej: Alan$1351a_4...") }, // [cite: 13]
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(25.dp))

            // BOTÓN: REGISTRARSE
            Button(
                onClick = {
                          /* Lógica de registro */
                          },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Registrarse", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = {
            /* Navegar al Login */
            })
            {
                Text(text = "¿Ya tienes cuenta? Inicia sesión", color = Color.White)
            }
            Spacer(modifier = Modifier.weight(1f))
            // Footer
            Text(
                text = "Todos los derechos reservados @deTec",
                fontSize = 12.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    DeTECTheme {
        RegisterScreen()
    }
}