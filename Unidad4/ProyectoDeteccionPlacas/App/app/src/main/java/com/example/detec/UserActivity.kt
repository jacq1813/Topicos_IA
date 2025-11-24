package com.example.detec

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.detec.ui.theme.DeTECTheme

class UserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeTECTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White // Fondo blanco general como en la imagen
                ) {
                    UserProfileScreen()
                }
            }
        }
    }
}

@Composable
fun UserProfileScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --------------------------------------------------
        // 1. ENCABEZADO (Header) con Imagen de Fondo
        // --------------------------------------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // Altura similar a tu imagen
        ) {
            // A. Imagen de fondo (Oscura)
            Image(
                painter = painterResource(id = R.drawable.bkg_app), // Reusamos tu imagen de fondo
                contentDescription = null,
                contentScale = ContentScale.Crop, // Recorta para llenar
                modifier = Modifier.fillMaxSize()
            )

            // B. Filtro oscuro por si la imagen es muy clara
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

            // C. Iconos (Menu - Logo - Usuario)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Icono Menú (Izquierda)
                IconButton(onClick = { /* Acción menú */ }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White, modifier = Modifier.size(32.dp))
                }

                // Logo Central (Simulamos el logo de la cámara con un icono y círculo)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(50)) // Círculo
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(4.dp)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Logo", tint = Color(0xFF4CAF50)) // Logo verde
                }

                // Icono Usuario (Derecha)
                IconButton(onClick = { /* Acción perfil */ }) {
                    Icon(Icons.Default.Person, contentDescription = "Perfil", tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --------------------------------------------------
        // 2. TÍTULO "USUARIO"
        // --------------------------------------------------
        Text(
            text = "USUARIO",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(30.dp))

        // --------------------------------------------------
        // 3. SECCIÓN DE INFO (Foto + Barras)
        // --------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre la foto y las barras
        ) {
            // A. Tarjeta de Foto de Perfil
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp) // Tamaño cuadrado grande
                    .clip(RoundedCornerShape(16.dp)) // Bordes redondeados
                    .background(Color(0xFFE0E0E0)) // Gris claro de fondo (como en la imagen)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(90.dp), // Icono grande
                    tint = Color.Black
                )
            }

            // B. Barras Grises (Derecha)
            Column(
                modifier = Modifier
                    .height(140.dp) // Misma altura que la foto para alinearlos
                    .weight(1f), // Ocupa el resto del ancho disponible
                verticalArrangement = Arrangement.SpaceBetween // Distribuye las barras de arriba a abajo
            ) {
                // Creamos 5 barras idénticas automáticamente
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(18.dp)
                            .clip(RoundedCornerShape(50)) // Bordes muy redondos
                            .background(Color(0xFFAAAAAA)) // Gris oscuro
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --------------------------------------------------
        // 4. BOTÓN "Configura tu perfil"
        // --------------------------------------------------
        Button(
            onClick = { /* Acción para configurar */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(55.dp), // Altura del botón
            shape = RoundedCornerShape(12.dp), // Bordes un poco redondeados
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50), // EL VERDE DE LA IMAGEN
                contentColor = Color.Black
            )
        ) {
            Text(
                text = "Configura tu perfil",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Empuja el footer hacia abajo
        Spacer(modifier = Modifier.weight(1f))

        // --------------------------------------------------
        // 5. FOOTER (@deTec en verde)
        // --------------------------------------------------
        Text(
            // Usamos buildAnnotatedString para poner solo una parte en verde
            text = buildAnnotatedString {
                append("Todos los derechos reservados ")
                withStyle(style = SpanStyle(color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)) {
                    append("@deTec")
                }
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfilePreview() {
    DeTECTheme {
        UserProfileScreen()
    }
}