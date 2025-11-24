package com.example.detec

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.* // Importa layout, padding, fillMaxSize, etc.
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.* // Importa Material Design 3 (Botones, Texto, etc.)
import androidx.compose.runtime.* // Importa remember y mutableStateOf para que funcionen los inputs
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.detec.ui.theme.DeTECTheme

class LogInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeTECTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Llamamos a nuestra nueva pantalla de Login
                    LoginScreen()
                }
            }
        }
    }
}

@Composable
fun LoginScreen() {
    // Variables de estado para guardar lo que el usuario escribe
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // 1. Usamos Box para apilar elementos (Fondo -> Contenido)
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // --- CAPA 1: LA IMAGEN DE FONDO ---
        Image(
            painter = painterResource(id = R.drawable.bkg_app), alpha = 0.85f,// Tu imagen aquí
            contentDescription = null,
            contentScale = ContentScale.Crop, // Importante: Recorta la imagen para llenar pantalla sin deformar
            modifier = Modifier.fillMaxSize() // Ocupa todo el espacio
            // alpha = 0.8f // Opcional: Si quieres que sea un poco transparente
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp), // Margen general
            horizontalAlignment = Alignment.CenterHorizontally // Centra todo horizontalmente
        ) {
            // Espacio superior para bajar un poco el título
            Spacer(modifier = Modifier.height(80.dp))

            // Título: deTec
            Text(
                text = "deTec",
                fontSize = 40.sp,
                color = Color(0xFF6200EE),
                fontWeight = Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Campo 1: Correo
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6200EE),   // Borde cuando escribes (Morado)
                    unfocusedBorderColor = Color.White,        // Borde cuando NO escribes
                    focusedTextColor = Color.White,           // Color del texto que escribes
                    unfocusedContainerColor = Color(0xFFF5F5F5),

                    ),
                label = { Text("Ingrese su correo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,

            )

            Spacer(modifier = Modifier.height(15.dp))

            // Campo 2: Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6200EE),   // Borde cuando escribes (Morado)
                    unfocusedBorderColor = Color.White,        // Borde cuando NO escribes
                    focusedTextColor = Color.White,           // Color del texto que escribes
                    unfocusedContainerColor = Color(0xFFF5F5F5),

                ),
                label = { Text("Ingrese su contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation() // Oculta el texto
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Botón: Inicia sesión
            Button(
                onClick = {
                    /* Aquí iría la lógica para el login */
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE), // Color de fondo (Morado ejemplo)
                    contentColor = Color.White          // Color del texto
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Iniciar sesión", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Link: Registro
            TextButton(onClick = {
                /* Navegar a la pagina de registro */
            }) {
                Text(text = "¿No tienes cuenta? Registrate aquí", color = Color.White)
            }

            // Empuja el contenido siguiente hacia el pie de pagina
            Spacer(modifier = Modifier.weight(1f))

            // Footer: Derechos reservados
            Text(
                text = "Derechos reservados @deTec",
                fontSize = 12.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(bottom = 16.dp)
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