package com.example.detec

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.detec.model.ReportRequest
import com.example.detec.network.RetrofitClient
import com.example.detec.ui.theme.DeTECTheme
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Date
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.FileOutputStream

class ReportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeTECTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ReportScreen(onReport = { finish() }, onNavigateBack = { finish() })
                }
            }
        }
    }
}

@Composable
fun ReportScreen(onNavigateBack: () -> Unit = {}, onReport: () -> Unit) {
    var hasCapturedPhoto by remember { mutableStateOf(false) }
    var detectedPlate by remember { mutableStateOf("Analizando...") }
    var isLoading by remember { mutableStateOf(false) }
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var currentPhotoFile by remember { mutableStateOf<File?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val session = SessionManager(context)

    // --- 1. PREPARAR CÁMARA ---
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoFile != null) {
            hasCapturedPhoto = true
            detectedPlate = "Procesando..."
            isLoading = true

            scope.launch {
                try {
                    // --- TRUCO: COMPRIMIR IMAGEN ANTES DE ENVIAR ---
                    // Esto reduce la foto de 5MB a 200KB. El servidor te lo agradecerá.
                    val originalFile = currentPhotoFile!!

                    // 1. Cargar la imagen en memoria reducida (Scale)
                    val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath)

                    // 2. Redimensionar si es muy grande (máx 800px)
                    val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
                    val width = 800
                    val height = (width / aspectRatio).toInt()
                    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)

                    // 3. Sobrescribir el archivo con la versión ligera
                    val outStream = FileOutputStream(originalFile)
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream) // Calidad 80%
                    outStream.flush()
                    outStream.close()
                    // -----------------------------------------------

                    val requestFile = originalFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("imagen", originalFile.name, requestFile)

                    val response = RetrofitClient.apiServiceIA.analizarPlaca(body)

                    if (response.isSuccessful) {
                        detectedPlate = response.body()?.placa ?: "No legible"
                        if (detectedPlate == "No detectada" || detectedPlate == "NODETECTADO") {
                            detectedPlate = "Intente de nuevo"
                        }
                    } else {
                        detectedPlate = "Error Servidor IA"
                    }
                } catch (e: Exception) {
                    detectedPlate = "Error Red"
                    e.printStackTrace()
                } finally {
                    isLoading = false
                }
            }
        }
    }

    // --- 2. FUNCIÓN PARA CREAR ARCHIVO TEMPORAL ---
    fun createImageFile(): File {
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile("JPEG_${Date().time}_", ".jpg", storageDir).apply {
            currentPhotoFile = this
        }
    }

    // --- 3. GESTOR DE PERMISOS (NUEVO) ---
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // Si el usuario dice SÍ, abrimos la cámara inmediatamente
            val file = createImageFile()
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            currentPhotoUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Se requiere permiso de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    if (hasCapturedPhoto) {
        ReportScreenWithImage(
            onNavigateBack = { hasCapturedPhoto = false },
            onRetakePhoto = { hasCapturedPhoto = false },
            imageDescription = detectedPlate,
            onConfirmPhoto = {
                if (!isLoading && detectedPlate != "Error Red" && detectedPlate != "Analizando...") {
                    isLoading = true
                    scope.launch {
                        try {
                            val userId = session.getUserId()
                            val nuevoReporte = ReportRequest(
                                usuarioId = userId,
                                numPlaca = detectedPlate,
                                descripcion = "Reporte automático",
                                coordenadas = "24.80, -107.40"
                            )
                            val response = RetrofitClient.apiService.crearReporte(nuevoReporte)
                            if (response.isSuccessful) {
                                Toast.makeText(context, "¡Reporte enviado!", Toast.LENGTH_LONG).show()
                                onReport()
                            } else {
                                Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally { isLoading = false }
                    }
                } else {
                    Toast.makeText(context, "Espere un momento...", Toast.LENGTH_SHORT).show()
                }
            }
        )
    } else {
        ReportCaptureView(
            onNavigateBack = onNavigateBack,
            onTakePhoto = {
                // VERIFICAR PERMISO ANTES DE ABRIR CÁMARA
                val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    // Si ya tiene permiso, abre cámara
                    val file = createImageFile()
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                    currentPhotoUri = uri
                    cameraLauncher.launch(uri)
                } else {
                    // Si no, pide permiso
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            onSelectFromGallery = { Toast.makeText(context, "Próximamente", Toast.LENGTH_SHORT).show() }
        )
    }
}

// --- VISTAS AUXILIARES (IGUAL QUE ANTES) ---

@Composable
fun ReportCaptureView(onNavigateBack: () -> Unit, onTakePhoto: () -> Unit, onSelectFromGallery: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onNavigateBack() }, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.ArrowBack, "Volver", tint = Color(0xFF6200EE), modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            Text("Nuevo Reporte", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EE))
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.size(48.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(16.dp))) {
            Image(painter = painterResource(id = R.drawable.bkg_app), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(Icons.Default.PhotoCamera, null, tint = Color.White, modifier = Modifier.size(60.dp))
                Text("Cámara lista", fontSize = 18.sp, color = Color.White)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { onTakePhoto() }, modifier = Modifier.fillMaxWidth(0.9f).height(56.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))) {
            Icon(Icons.Default.CameraAlt, null); Spacer(modifier = Modifier.width(12.dp)); Text("TOMAR FOTO", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ReportScreenWithImage(onNavigateBack: () -> Unit, onRetakePhoto: () -> Unit, onConfirmPhoto: () -> Unit, imageDescription: String) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Confirmar Evidencia", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EE))
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.fillMaxWidth().height(300.dp).clip(RoundedCornerShape(16.dp))) {
            Image(painter = painterResource(id = R.drawable.bkg_app), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            if (imageDescription == "Procesando..." || imageDescription == "Analizando...") {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Placa detectada:", fontSize = 14.sp, color = Color.Gray)
                Text(imageDescription, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = { onRetakePhoto() }, modifier = Modifier.weight(1f).height(56.dp), shape = RoundedCornerShape(12.dp)) { Text("REINTENTAR") }
            Button(onClick = { onConfirmPhoto() }, modifier = Modifier.weight(1f).height(56.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), enabled = imageDescription != "Procesando..." && imageDescription != "Analizando...") { Text("CONFIRMAR") }
        }
    }
}