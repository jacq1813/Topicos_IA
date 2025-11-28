package com.example.detec

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoCamera
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.detec.model.ReportRequest
import com.example.detec.network.RetrofitClient
import com.example.detec.ui.theme.DeTECTheme
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class ReportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Despertar al servidor IA
        despertarServidor()

        setContent {
            DeTECTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ReportScreen(onReport = { finish() }, onNavigateBack = { finish() })
                }
            }
        }
    }

    private fun despertarServidor() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // URL de Hugging Face
                val url = "https://jacqueline-placas.hf.space/"
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute()
            } catch (e: Exception) { Log.e("WakeUp", "Error: ${e.message}") }
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

    // --- LÓGICA GPS ---
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    // Coordenada default por si falla el GPS
    var coordenadasGPS by remember { mutableStateOf("24.80, -107.40") }

    // Intentamos obtener ubicación al entrar a la pantalla
    // En ReportActivity.kt, dentro de ReportScreen

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Opción 1: Intenta obtener la última conocida (es muy rápida)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    coordenadasGPS = "${location.latitude}, ${location.longitude}"
                    Log.d("GPS_DETEC", "Ubicación caché: $coordenadasGPS")
                } else {
                    // Opción 2 (PLAN B): Si la memoria está vacía (NULL), forzamos búsqueda nueva
                    // Esto es lo que arregla el problema en tu emulador
                    Toast.makeText(context, "Activando GPS...", Toast.LENGTH_SHORT).show()

                    // Priority.PRIORITY_HIGH_ACCURACY obliga a usar el GPS real
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                        .addOnSuccessListener { freshLocation ->
                            if (freshLocation != null) {
                                coordenadasGPS = "${freshLocation.latitude}, ${freshLocation.longitude}"
                                Toast.makeText(context, "📍 Ubicación actual: $coordenadasGPS", Toast.LENGTH_LONG).show()
                                Log.d("GPS_DETEC", "Ubicación fresca: $coordenadasGPS")
                            } else {
                                Log.e("GPS_DETEC", "Imposible detectar ubicación")
                            }
                        }
                }
            }
        }
    }

    // --- CÁMARA ORIGINAL (Intent) ---
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoFile != null) {
            hasCapturedPhoto = true
            detectedPlate = "Procesando..."
            isLoading = true

            // Procesamiento en segundo plano
            scope.launch(Dispatchers.IO) {
                try {
                    val originalFile = currentPhotoFile!!
                    val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath)

                    // Comprimir imagen antes de enviar
                    val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
                    val width = 800
                    val height = (width / aspectRatio).toInt()
                    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
                    val outStream = FileOutputStream(originalFile)
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outStream)
                    outStream.flush(); outStream.close()

                    // Enviar a IA (Hugging Face)
                    val requestFile = originalFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("imagen", originalFile.name, requestFile)
                    val response = RetrofitClient.apiServiceIA.analizarPlaca(body)

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            detectedPlate = response.body()?.placa ?: ""
                            if (detectedPlate == "NODETECTADO") detectedPlate = ""
                        } else {
                            detectedPlate = ""
                            Toast.makeText(context, "No se detectó placa", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) { detectedPlate = "" }
                } finally {
                    withContext(Dispatchers.Main) { isLoading = false }
                }
            }
        }
    }

    fun createImageFile(): File {
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile("JPEG_${Date().time}_", ".jpg", storageDir).apply {
            currentPhotoFile = this
        }
    }

    // Permisos: Pedimos CÁMARA y UBICACIÓN al mismo tiempo
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

        if (cameraGranted) {
            val file = createImageFile()
            val uri = FileProvider.getUriForFile(context, "com.example.detec.provider", file)
            currentPhotoUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Se requiere permiso de cámara", Toast.LENGTH_SHORT).show()
        }

        // Si nos dieron permiso de GPS, actualizamos la coordenada ahora mismo
        if (locationGranted) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) coordenadasGPS = "${loc.latitude}, ${loc.longitude}"
                }
            }
        }
    }

    if (hasCapturedPhoto) {
        // PANTALLA DE FORMULARIO
        ReportFormScreen(
            detectedPlate = detectedPlate,
            imageFile = currentPhotoFile,
            isLoadingIA = isLoading,
            onNavigateBack = { hasCapturedPhoto = false },
            onRetakePhoto = { hasCapturedPhoto = false },
            onConfirmReport = { finalPlate, finalDesc ->
                // LOGICA DE ENVIO FINAL
                isLoading = true
                scope.launch(Dispatchers.IO) {
                    try {
                        val idUser = session.getUserId()
                        val userId = if (idUser is Int) idUser else 0

                        // AQUÍ USAMOS LA VARIABLE coordenadasGPS QUE OBTUVIMOS
                        val nuevoReporte = ReportRequest(
                            usuarioId = userId,
                            numPlaca = finalPlate,
                            descripcion = finalDesc,
                            coordenadas = coordenadasGPS,
                            imgEvidencia = "foto_evidencia.jpg"
                        )

                        val response = RetrofitClient.apiService.crearReporte(nuevoReporte)

                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Toast.makeText(context, "¡Reporte enviado exitosamente!", Toast.LENGTH_LONG).show()
                                onReport()
                            } else {
                                Toast.makeText(context, "Error servidor: ${response.code()}", Toast.LENGTH_LONG).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) { Toast.makeText(context, "Error de red", Toast.LENGTH_LONG).show() }
                    } finally {
                        withContext(Dispatchers.Main) { isLoading = false }
                    }
                }
            }
        )
    } else {
        // PANTALLA DE TOMA DE FOTO (Original)
        ReportCaptureView(
            onNavigateBack = onNavigateBack,
            onTakePhoto = {
                // Lanzamos petición múltiple
                permissionLauncher.launch(arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ))
            },
            onSelectFromGallery = { Toast.makeText(context, "Próximamente", Toast.LENGTH_SHORT).show() }
        )
    }
}

// --- VISTAS AUXILIARES (Tus mismas vistas, sin cambios lógicos) ---

@Composable
fun ReportFormScreen(detectedPlate: String, imageFile: File?, isLoadingIA: Boolean, onNavigateBack: () -> Unit, onRetakePhoto: () -> Unit, onConfirmReport: (String, String) -> Unit) {
    var plateInput by remember { mutableStateOf(detectedPlate) }
    var descriptionInput by remember { mutableStateOf("") }
    LaunchedEffect(detectedPlate) { if (detectedPlate != "Analizando..." && detectedPlate != "Procesando...") plateInput = detectedPlate }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Completar Reporte", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EE))
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp))) {
            Image(painter = painterResource(id = R.drawable.bkg_app), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            if (isLoadingIA) Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.5f)), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color.White) }
        }
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(value = plateInput, onValueChange = { plateInput = it.uppercase() }, label = { Text("Número de Placa") }, modifier = Modifier.fillMaxWidth(), singleLine = true, enabled = !isLoadingIA)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = descriptionInput, onValueChange = { descriptionInput = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 5)
        Spacer(modifier = Modifier.height(30.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = onRetakePhoto, modifier = Modifier.weight(1f)) { Text("Reintentar") }
            Button(onClick = { if(plateInput.isNotEmpty() && descriptionInput.isNotEmpty()) onConfirmReport(plateInput, descriptionInput) }, modifier = Modifier.weight(1f), enabled = !isLoadingIA, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) { Text("ENVIAR") }
        }
    }
}

@Composable
fun ReportCaptureView(onNavigateBack: () -> Unit, onTakePhoto: () -> Unit, onSelectFromGallery: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null, tint = Color(0xFF6200EE)) }
            Spacer(modifier = Modifier.weight(1f))
            Text("Nuevo Reporte", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EE))
            Spacer(modifier = Modifier.weight(1f)); Box(modifier = Modifier.size(48.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(16.dp)).background(Color.Black)) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.PhotoCamera, null, tint = Color.White, modifier = Modifier.size(60.dp))
                Text("Cámara lista", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = onTakePhoto, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))) {
            Icon(Icons.Default.CameraAlt, null); Spacer(modifier = Modifier.width(12.dp)); Text("TOMAR FOTO")
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}