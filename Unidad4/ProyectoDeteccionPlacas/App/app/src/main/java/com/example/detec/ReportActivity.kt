package com.example.detec

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log // <--- Importante para logs
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
import androidx.lifecycle.lifecycleScope // <--- Necesario para el despertador
import com.example.detec.model.ReportRequest
import com.example.detec.network.RetrofitClient
import com.example.detec.ui.theme.DeTECTheme
import kotlinx.coroutines.Dispatchers // <--- Necesario para hilos de fondo
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext // <--- Para cambiar de hilo
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient // <--- Cliente para el despertador
import okhttp3.Request      // <--- Request para el despertador
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.Date

class ReportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. EL DESPERTADOR ⏰
        // Esto envía una señal a Hugging Face apenas se crea la pantalla
        despertarServidor()

        setContent {
            DeTECTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ReportScreen(onReport = { finish() }, onNavigateBack = { finish() })
                }
            }
        }
    }

    // Función para despertar a Hugging Face
    private fun despertarServidor() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Pon aquí tu URL base de Hugging Face
                val url = "https://tu-space-usuario.hf.space/"
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute()
                Log.d("IA_WAKEUP", "Ping enviado al servidor")
            } catch (e: Exception) {
                Log.e("IA_WAKEUP", "Fallo silencioso al despertar: ${e.message}")
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
    val session = SessionManager(context) // Asegúrate de tener esta clase o borrar esto si no la usas aquí

    // --- 1. PREPARAR CÁMARA ---
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoFile != null) {
            hasCapturedPhoto = true
            detectedPlate = "Procesando..."
            isLoading = true

            // 2. CORRECCIÓN IMPORTANTE: Dispatchers.IO 🚀
            // Usamos Dispatchers.IO para que la compresión y la red no congelen la pantalla
            scope.launch(Dispatchers.IO) {
                try {
                    // --- COMPRIMIR IMAGEN ---
                    val originalFile = currentPhotoFile!!
                    val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath)

                    // Calculamos nuevas dimensiones
                    val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
                    val width = 800
                    val height = (width / aspectRatio).toInt()

                    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)

                    // Sobrescribimos el archivo con la versión ligera
                    val outStream = FileOutputStream(originalFile)
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outStream) // Calidad 70 es suficiente
                    outStream.flush()
                    outStream.close()

                    // --- ENVIAR A IA ---
                    val requestFile = originalFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("imagen", originalFile.name, requestFile)

                    val response = RetrofitClient.apiServiceIA.analizarPlaca(body)

                    // Volvemos al Hilo Principal (Main) para actualizar la UI
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val placaDetectada = response.body()?.placa ?: ""
                            detectedPlate = if (placaDetectada == "NODETECTADO") "" else placaDetectada
                        } else {
                            detectedPlate = ""
                            Toast.makeText(context, "No se detectó placa, ingrésela manualmente", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        detectedPlate = ""
                        e.printStackTrace()
                        Toast.makeText(context, "Error de conexión con IA", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    withContext(Dispatchers.Main) {
                        isLoading = false
                    }
                }
            }
        }
    }

    // ... (El resto de tu código: createImageFile, permissionLauncher, UI, etc. sigue igual)

    fun createImageFile(): File {
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile("JPEG_${Date().time}_", ".jpg", storageDir).apply {
            currentPhotoFile = this
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val file = createImageFile()
            val uri = FileProvider.getUriForFile(context, "com.example.detec.provider", file)
            currentPhotoUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Se requiere permiso de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    // ... (Tu UI de if(hasCapturedPhoto) ... sigue igual)
    if (hasCapturedPhoto) {
        ReportFormScreen(
            detectedPlate = detectedPlate,
            imageFile = currentPhotoFile,
            isLoadingIA = isLoading,
            onNavigateBack = { hasCapturedPhoto = false },
            onRetakePhoto = { hasCapturedPhoto = false },
            onConfirmReport = { finalPlate, finalDesc ->
                // LOGICA DE ENVIO FINAL A LA BD
                isLoading = true
                scope.launch(Dispatchers.IO) { // <--- También aquí usa IO
                    try {
                        val idObtenido = session.getUserId()
                        val userId = if (idObtenido is Int) idObtenido else 0
                        val nuevoReporte = ReportRequest(
                            usuarioId = userId,
                            numPlaca = finalPlate,
                            descripcion = finalDesc,
                            coordenadas = "24.80, -107.40",
                            imgEvidencia = "foto_evidencia.jpg"
                        )

                        val response = RetrofitClient.apiService.crearReporte(nuevoReporte)

                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Toast.makeText(context, "¡Reporte enviado con éxito!", Toast.LENGTH_LONG).show()
                                onReport()
                            } else {
                                val errorBody = response.errorBody()?.string()
                                Toast.makeText(context, "Error: $errorBody", Toast.LENGTH_LONG).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    } finally {
                        withContext(Dispatchers.Main) {
                            isLoading = false
                        }
                    }
                }
            }
        )
    } else {
        ReportCaptureView(
            onNavigateBack = onNavigateBack,
            onTakePhoto = {
                val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    val file = createImageFile()
                    val uri = FileProvider.getUriForFile(context, "com.example.detec.provider", file)
                    currentPhotoUri = uri
                    cameraLauncher.launch(uri)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            onSelectFromGallery = { Toast.makeText(context, "Próximamente", Toast.LENGTH_SHORT).show() }
        )
    }
}

// ... (El resto de tus Composables ReportFormScreen y ReportCaptureView siguen igual)
@Composable
fun ReportFormScreen(
    detectedPlate: String,
    imageFile: File?,
    isLoadingIA: Boolean,
    onNavigateBack: () -> Unit,
    onRetakePhoto: () -> Unit,
    onConfirmReport: (String, String) -> Unit
) {
    // Estados para los campos editables
    var plateInput by remember { mutableStateOf(detectedPlate) }
    var descriptionInput by remember { mutableStateOf("") }

    // Actualizar el campo de placa cuando la IA termine
    LaunchedEffect(detectedPlate) {
        if (detectedPlate != "Analizando..." && detectedPlate != "Procesando...") {
            plateInput = detectedPlate
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Habilitar scroll
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Completar Reporte", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EE))

        Spacer(modifier = Modifier.height(20.dp))

        // Imagen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.bkg_app), // Placeholder
                contentDescription = "Evidencia",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Si tuvieramos Coil usariamos imageFile aqui

            if (isLoadingIA) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                    Text("Analizando placa...", color = Color.White, modifier = Modifier.padding(top = 50.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // CAMPO 1: PLACA (Editable)
        OutlinedTextField(
            value = plateInput,
            onValueChange = { plateInput = it.uppercase() }, // Forzar mayúsculas
            label = { Text("Número de Placa") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6200EE),
                focusedLabelColor = Color(0xFF6200EE)
            ),
            singleLine = true,
            enabled = !isLoadingIA // Bloquear mientras la IA piensa
        )

        Spacer(modifier = Modifier.height(16.dp))

        // CAMPO 2: DESCRIPCIÓN
        OutlinedTextField(
            value = descriptionInput,
            onValueChange = { descriptionInput = it },
            label = { Text("Motivo del reporte / Descripción") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp), // Más alto para escribir
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6200EE),
                focusedLabelColor = Color(0xFF6200EE)
            ),
            maxLines = 5,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // BOTONES
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { onRetakePhoto() },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Reintentar") }

            Button(
                onClick = {
                    if (plateInput.isNotEmpty() && descriptionInput.isNotEmpty()) {
                        onConfirmReport(plateInput, descriptionInput)
                    } else {
                        // Mensaje simple si falta algo
                    }
                },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                enabled = !isLoadingIA && plateInput.isNotEmpty() && descriptionInput.isNotEmpty()
            ) { Text("ENVIAR") }
        }
    }
}

// 3. VISTA CAPTURA (Sin cambios, solo la agrego para que compile todo junto)
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