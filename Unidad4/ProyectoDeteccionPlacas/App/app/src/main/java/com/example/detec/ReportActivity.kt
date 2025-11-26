// En ReportActivity.kt

// Importaciones necesarias adicionales
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.example.detec.SessionManager
import com.example.detec.model.ReportRequest
import com.example.detec.network.RetrofitClient
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

@Composable
fun ReportScreen(
    onNavigateBack: () -> Unit = {},
    onReport: () -> Unit
) {
    var hasCapturedPhoto by remember { mutableStateOf(false) }
    var detectedPlate by remember { mutableStateOf("Analizando...") } // Texto de la placa
    var isLoading by remember { mutableStateOf(false) }
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var currentPhotoFile by remember { mutableStateOf<File?>(null) } // Archivo real

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val session = SessionManager(context)

    // 1. PREPARAR EL LANZADOR DE CÁMARA
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoFile != null) {
            hasCapturedPhoto = true
            detectedPlate = "Procesando..."

            // 2. ENVIAR AUTOMÁTICAMENTE A LA IA AL TOMAR LA FOTO
            isLoading = true
            scope.launch {
                try {
                    val file = currentPhotoFile!!
                    // Crear el cuerpo de la petición Multipart
                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("imagen", file.name, requestFile)

                    val response = RetrofitClient.apiService.analizarImagen(body)

                    if (response.isSuccessful) {
                        detectedPlate = response.body()?.placa ?: "No legible"
                        if (detectedPlate == "No detectada") detectedPlate = "Intente de nuevo"
                    } else {
                        detectedPlate = "Error Servidor"
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

    // Función auxiliar para crear el archivo temporal
    fun createImageFile(): File {
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile("JPEG_${Date().time}_", ".jpg", storageDir).apply {
            currentPhotoFile = this
        }
    }

    if (hasCapturedPhoto) {
        // PANTALLA DE CONFIRMACIÓN (Muestra resultado de IA)
        ReportScreenWithImage(
            onNavigateBack = { hasCapturedPhoto = false },
            onRetakePhoto = { hasCapturedPhoto = false },
            imageDescription = detectedPlate, // <--- AQUÍ MOSTRAMOS LA PLACA DETECTADA
            onConfirmPhoto = {
                // 3. GUARDAR REPORTE FINAL EN BD
                if (!isLoading && detectedPlate != "Error" && detectedPlate != "Analizando...") {
                    isLoading = true
                    scope.launch {
                        try {
                            val userId = session.getUserId()
                            val nuevoReporte = ReportRequest(
                                usuarioId = userId,
                                numPlaca = detectedPlate, // Usamos la placa detectada por la IA
                                descripcion = "Reporte automático App",
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
                            // ... error handling
                        } finally { isLoading = false }
                    }
                }
            }
        )
    } else {
        // PANTALLA DE CAPTURA
        ReportCaptureView(
            onNavigateBack = onNavigateBack,
            onTakePhoto = {
                // Crear archivo y lanzar cámara
                val file = createImageFile()
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider", // Debe coincidir con AndroidManifest
                    file
                )
                currentPhotoUri = uri
                cameraLauncher.launch(uri)
            },
            onSelectFromGallery = { Toast.makeText(context, "Próximamente", Toast.LENGTH_SHORT).show() }
        )
    }
}