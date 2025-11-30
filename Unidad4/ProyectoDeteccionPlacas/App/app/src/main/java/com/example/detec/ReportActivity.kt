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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.detec.model.ReportRequest
import com.example.detec.network.RetrofitClient
import com.example.detec.ui.theme.DeTECTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CoroutineScope
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

class ReportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                val url = "https://jacq13-ia-placas-detect.hf.space"
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

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var coordenadasGPS by remember { mutableStateOf("24.80, -107.40") }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    coordenadasGPS = "${location.latitude}, ${location.longitude}"
                } else {
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                        .addOnSuccessListener { fresh ->
                            if (fresh != null) coordenadasGPS = "${fresh.latitude}, ${fresh.longitude}"
                        }
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success ->
        if (success && currentPhotoFile != null) {
            hasCapturedPhoto = true
            detectedPlate = "Procesando..."
            isLoading = true

            scope.launch(Dispatchers.IO) {
                try {
                    val file = currentPhotoFile!!
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    val width = 800
                    val height = (width / (bitmap.width.toFloat() / bitmap.height.toFloat())).toInt()
                    val resized = Bitmap.createScaledBitmap(bitmap, width, height, false)
                    val outStream = FileOutputStream(file)
                    resized.compress(Bitmap.CompressFormat.JPEG, 70, outStream)
                    outStream.close()

                    val req = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("imagen", file.name, req)
                    val response = RetrofitClient.apiServiceIA.analizarPlaca(body)

                    withContext(Dispatchers.Main) {
                        detectedPlate = if (response.isSuccessful) response.body()?.placa ?: "" else ""
                        if (detectedPlate == "NODETECTADO") detectedPlate = ""
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
        return File.createTempFile("JPEG_${Date().time}_", ".jpg", storageDir).apply { currentPhotoFile = this }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
        if (perms[Manifest.permission.CAMERA] == true) {
            val file = createImageFile()
            currentPhotoUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            cameraLauncher.launch(currentPhotoUri)
        }
    }

    if (hasCapturedPhoto) {
        ReportFormScreen(
            detectedPlate = detectedPlate,
            imageFile = currentPhotoFile,
            isLoadingIA = isLoading,
            onNavigateBack = { hasCapturedPhoto = false },
            onRetakePhoto = { hasCapturedPhoto = false },
            onConfirmReport = { finalPlate, finalDesc ->
                isLoading = true
                scope.launch(Dispatchers.IO) {
                    try {
                        val userId = if (session.getUserId() is Int) session.getUserId() else 0
                        val nuevoReporte = ReportRequest(
                            usuarioId = userId,
                            numPlaca = finalPlate,
                            descripcion = finalDesc,
                            coordenadas = coordenadasGPS,
                            imgEvidencia = "foto.jpg"
                        )

                        val response = RetrofitClient.apiService.crearReporte(nuevoReporte)

                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Toast.makeText(context, "¡Reporte Guardado!", Toast.LENGTH_SHORT).show()

                                val correoUsuario = session.getUserEmail()
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        EmailSender.enviar(
                                            destinatario = correoUsuario,
                                            placa = finalPlate,
                                            descripcion = finalDesc
                                        )
                                    } catch (e: Exception) {
                                        Log.e("Correo", "Error envío fondo: ${e.message}")
                                    }
                                }

                                onReport()
                            } else {
                                Toast.makeText(context, "Error no se pudo guardar el reporte", Toast.LENGTH_LONG).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error de red: Verifique internet", Toast.LENGTH_LONG).show()
                        }
                    } finally {
                        withContext(Dispatchers.Main) { isLoading = false }
                    }
                }
            }
        )
    } else {
        ReportCaptureView(
            onNavigateBack = onNavigateBack,
            onTakePhoto = { permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)) },
            onSelectFromGallery = { Toast.makeText(context, "Próximamente", Toast.LENGTH_SHORT).show() }
        )
    }
}

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