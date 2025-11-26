package com.example.detec

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.* // Importante para remember y mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.example.detec.network.RetrofitClient
import com.example.detec.model.ReportRequest
import kotlinx.coroutines.launch
import android.widget.Toast

class ReportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeTECTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Solo para probar la Activity individualmente
                    ReportScreen(onReport = { finish() }, onNavigateBack = { finish() })
                }
            }
        }
    }
}

// ----------------------------------------------------------------
// 1. PANTALLA PRINCIPAL (Controlador)
// ----------------------------------------------------------------
@Composable
fun ReportScreen(
    onNavigateBack: () -> Unit = {},
    onTakePhoto: () -> Unit = {},
    onSelectFromGallery: () -> Unit = {},
    onReport: () -> Unit
) {
    // Variables de estado y contexto
    var hasCapturedPhoto by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) } // Para evitar doble clic
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val session = SessionManager(context)

    if (hasCapturedPhoto) {
        // MUESTRA LA PANTALLA DE CONFIRMACIÓN
        ReportScreenWithImage(
            onNavigateBack = { hasCapturedPhoto = false },
            onRetakePhoto = { hasCapturedPhoto = false },

            // --- AQUÍ OCURRE LA MAGIA DE LA BD ---
            onConfirmPhoto = {
                if (!isLoading) {
                    isLoading = true
                    scope.launch {
                        try {
                            // 1. Obtener ID del usuario actual
                            val userId = session.getUserId()

                            // 2. Crear el objeto reporte (Asegúrate que coincida con tu ApiModels.kt)
                            // Nota: Como aún no integramos la IA real, mandamos una placa "simulada"
                            val nuevoReporte = ReportRequest(
                                usuarioId = userId,
                                numPlaca = "ABC-123", // Cuando tengas la IA, aquí pones la variable de la placa
                                descripcion = "Reporte enviado desde Android",
                                coordenadas = "24.80, -107.40", // Coordenadas ejemplo (Culiacán)
                                imgEvidencia = "url_pendiente"
                            )

                            // 3. Enviar a la API
                            val response = RetrofitClient.apiService.crearReporte(nuevoReporte)

                            if (response.isSuccessful) {
                                Toast.makeText(context, "¡Reporte guardado en BD!", Toast.LENGTH_LONG).show()
                                onReport() // Navega al Home
                            } else {
                                Toast.makeText(context, "Error al guardar: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                            e.printStackTrace()
                        } finally {
                            isLoading = false
                        }
                    }
                }
            }
            // -------------------------------------
        )
    } else {
        // MUESTRA LA PANTALLA DE CAPTURA
        ReportCaptureView(
            onNavigateBack = onNavigateBack,
            onTakePhoto = {
                hasCapturedPhoto = true
                onTakePhoto()
            },
            onSelectFromGallery = onSelectFromGallery
        )
    }
}
// ----------------------------------------------------------------
// 2. VISTA DE CAPTURA (Cámara)
// ----------------------------------------------------------------
@Composable
fun ReportCaptureView(
    onNavigateBack: () -> Unit,
    onTakePhoto: () -> Unit,
    onSelectFromGallery: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onNavigateBack() }, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.ArrowBack, "Volver", tint = Color(0xFF6200EE), modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            Text("Nuevo Reporte", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EE))
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Placeholder Cámara
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.bkg_app),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.PhotoCamera, null, tint = Color.White, modifier = Modifier.size(60.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Vista previa", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Aviso
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, null, tint = Color(0xFF2196F3), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Toma captura de la evidencia", fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                    Text("Enfoca la placa del vehículo.", fontSize = 14.sp, color = Color(0xFF1976D2))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botones
        Button(
            onClick = { onTakePhoto() }, // <--- ESTO CAMBIA EL ESTADO A TRUE
            modifier = Modifier.fillMaxWidth(0.9f).height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Icon(Icons.Default.CameraAlt, null)
            Spacer(modifier = Modifier.width(12.dp))
            Text("TOMAR FOTO", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { onSelectFromGallery() },
            modifier = Modifier.fillMaxWidth(0.9f).height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("SELECCIONAR DE GALERÍA")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ----------------------------------------------------------------
// 3. VISTA DE CONFIRMACIÓN (Ya con foto)
// ----------------------------------------------------------------
@Composable
fun ReportScreenWithImage(
    onNavigateBack: () -> Unit = {},
    onRetakePhoto: () -> Unit = {},
    onConfirmPhoto: () -> Unit = {},
    imageDescription: String = "Placa ABC-123"
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onNavigateBack() }, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.ArrowBack, "Volver", tint = Color(0xFF6200EE), modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            Text("Confirmar Evidencia", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EE))
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.bkg_app), // Aquí iría la foto real
                contentDescription = "Evidencia",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF4CAF50))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Capturado", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Placa detectada:", fontSize = 14.sp, color = Color.Gray)
                Text(imageDescription, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = { onRetakePhoto() },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("REINTENTAR")
            }

            Button(
                onClick = { onConfirmPhoto() }, // <--- ESTO REGRESA AL HOME
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("CONFIRMAR", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}