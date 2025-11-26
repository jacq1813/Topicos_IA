package com.example.detec

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack // <--- IMPORTANTE: Agregado
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.detec.ui.theme.DeTECTheme
import androidx.compose.ui.platform.LocalContext // <--- ASEGURATE DE TENER ESTE IMPORT

class UserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeTECTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    UserProfileScreen()
                }
            }
        }
    }
}

@Composable
fun UserProfileScreen(
    onNavigateBack: () -> Unit = {}
) {
    // -----------------------------------------------------------
    // CAMBIO: LEER DATOS REALES DE LA SESIÓN
    // -----------------------------------------------------------
    val context = LocalContext.current
    val session = SessionManager(context)

    // Si no hay nombre guardado, usa "Usuario" por defecto
    val userName = session.getUserName()
    val userEmail = session.getUserEmail()
    // -----------------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --------------------------------------------------
        // 1. ENCABEZADO ESTANDARIZADO (Igual a ReportActivity)
        // --------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onNavigateBack() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color(0xFF6200EE),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Perfil de Usuario",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6200EE)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Caja vacía para equilibrar y centrar el título perfectamente
            Box(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --------------------------------------------------
        // 2. SECCIÓN DE INFO (Foto + Datos del Usuario)
        // --------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp), // Ajuste ligero de márgenes
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // A. Tarjeta de Foto de Perfil (IZQUIERDA)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp)) // Borde consistente de 16dp
                    .background(Color(0xFFE0E0E0))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(60.dp), // Ícono un poco más grande
                        tint = Color(0xFF6200EE)
                    )
                }
            }

            // B. INFORMACIÓN DEL USUARIO (DERECHA)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tarjeta 1: Nombre de usuario
                InfoCard(
                    icon = Icons.Default.Person,
                    title = "Nombre completo",
                    value = userName,
                    iconColor = Color(0xFF6200EE),
                    backgroundColor = Color(0xFFF3E5F5)
                )

                // Tarjeta 2: Correo electrónico
                InfoCard(
                    icon = Icons.Default.Email,
                    title = "Correo",
                    value = userEmail,
                    iconColor = Color(0xFF2196F3),
                    backgroundColor = Color(0xFFE3F2FD)
                )
            }
        }

        // Tarjeta extra de fecha (movida abajo para mejor distribución en este layout)
        Spacer(modifier = Modifier.height(12.dp))
        InfoCard(
            icon = Icons.Default.DateRange,
            title = "Miembro desde",
            value = "Enero 2024",
            iconColor = Color(0xFF4CAF50),
            backgroundColor = Color(0xFFE8F5E8)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // --------------------------------------------------
        // 3. ESTADÍSTICAS
        // --------------------------------------------------
        Text(
            text = "ESTADÍSTICAS",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6200EE),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta de Reportes
            StatCard(
                icon = Icons.Default.List,
                value = "15",
                title = "Reportes",
                subtitle = "Realizados",
                color = Color(0xFF6200EE)
            )

            // Tarjeta de Actividad
            StatCard(
                icon = Icons.Default.CheckCircle,
                value = "Activo",
                title = "Estado",
                subtitle = "Cuenta",
                color = Color(0xFF4CAF50)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --------------------------------------------------
        // 4. BOTÓN "Configura tu perfil" (Estandarizado)
        // --------------------------------------------------
        Button(
            onClick = { /* Acción para configurar perfil */ },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(56.dp), // Altura estándar 56dp
            shape = RoundedCornerShape(12.dp), // Borde estándar 12dp
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6200EE), // Color principal
                contentColor = Color.White
            )
        ) {
            Text(
                text = "CONFIGURAR PERFIL", // Texto en mayúsculas estilo botón
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Empuja el footer hacia abajo
        Spacer(modifier = Modifier.weight(1f))

        // --------------------------------------------------
        // 5. FOOTER
        // --------------------------------------------------
        Text(
            text = buildAnnotatedString {
                append("Todos los derechos reservados ")
                withStyle(style = SpanStyle(color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)) {
                    append("@deTec")
                }
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 24.dp)
        )
    }
}

// Componente reutilizable para tarjetas de información
@Composable
fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    iconColor: Color,
    backgroundColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1 // Evita que rompa el diseño
            )
        }
    }
}

// Componente reutilizable para tarjetas de estadísticas
@Composable
fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    title: String,
    subtitle: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.48f) // Ocupa casi la mitad
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfilePreview() {
    DeTECTheme {
        UserProfileScreen()
    }
}