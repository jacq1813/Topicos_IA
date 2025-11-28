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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.platform.LocalContext
import com.example.detec.network.RetrofitClient // Importante para la API
import kotlinx.coroutines.launch

class UserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeTECTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    UserProfileScreen(onNavigateBack = { finish() })
                }
            }
        }
    }
}

@Composable
fun UserProfileScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val session = SessionManager(context)

    // 1. Obtener Datos del Usuario de la Sesión
    val userName = session.getUserName()
    val userEmail = session.getUserEmail()

    // 2. Lógica para contar los reportes reales desde la API
    var reportCount by remember { mutableStateOf("...") } // "..." mientras carga

    LaunchedEffect(Unit) {
        val userId = session.getUserId()
        if (userId != -1) {
            try {
                // Llamamos a la API
                val response = RetrofitClient.apiService.getReportesUsuario(userId)
                if (response.isSuccessful && response.body() != null) {
                    // Contamos el tamaño de la lista
                    val cantidad = response.body()!!.size
                    reportCount = cantidad.toString()
                } else {
                    reportCount = "0"
                }
            } catch (e: Exception) {
                reportCount = "-" // Muestra guion si hay error de red
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- ENCABEZADO ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onNavigateBack() }, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.ArrowBack, "Volver", tint = Color(0xFF6200EE), modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            Text("Perfil de Usuario", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EE))
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- TARJETA DE INFORMACIÓN ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Foto
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFFE0E0E0))
            ) {
                Icon(Icons.Default.AccountCircle, "Foto", modifier = Modifier.size(60.dp), tint = Color(0xFF6200EE))
            }

            // Datos
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoCard(Icons.Default.Person, "Nombre completo", userName, Color(0xFF6200EE), Color(0xFFF3E5F5))
                InfoCard(Icons.Default.Email, "Correo", userEmail, Color(0xFF2196F3), Color(0xFFE3F2FD))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        InfoCard(Icons.Default.DateRange, "Miembro desde", "Noviembre 2025", Color(0xFF4CAF50), Color(0xFFE8F5E8))

        Spacer(modifier = Modifier.height(30.dp))

        // --- ESTADÍSTICAS ---
        Text("ESTADÍSTICAS", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EE), modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp))
        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre ellas
        ) {
            // Tarjeta Reportes
            StatCard(
                icon = Icons.Default.List,
                value = reportCount,
                title = "Reportes",
                subtitle = "Realizados",
                color = Color(0xFF6200EE),
                modifier = Modifier.weight(1f) // <--- ESTO LA HACE CRECER AL 50%
            )

            // Tarjeta Estado (Verde)
            StatCard(
                icon = Icons.Default.CheckCircle,
                value = "Activo",
                title = "Estado",
                subtitle = "Cuenta",
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f) // <--- ESTO LA HACE DEL MISMO TAMAÑO QUE LA OTRA
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- BOTÓN CONFIGURAR ---
        Button(
            onClick = { /* Acción futura */ },
            modifier = Modifier.fillMaxWidth(0.9f).height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE), contentColor = Color.White)
        ) {
            Text("CONFIGURAR PERFIL", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.weight(1f))

        // --- FOOTER ---
        Text(
            text = buildAnnotatedString {
                append("Todos los derechos reservados ")
                withStyle(style = SpanStyle(color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)) { append("@deTec") }
            },
            fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Gray, modifier = Modifier.padding(vertical = 24.dp)
        )
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun InfoCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String, iconColor: Color, backgroundColor: Color) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(backgroundColor).padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Text(text = value, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
    }
}

// Componente reutilizable para tarjetas de estadísticas (CORREGIDO)
// Reemplaza tu función StatCard con esta versión flexible:
@Composable
fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier // <--- Nuevo parámetro para controlar el tamaño
) {
    Box(
        modifier = modifier // <--- Usamos el modificador que viene de fuera (weight)
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(16.dp), // Padding interno cómodo
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                color = color,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                maxLines = 1
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