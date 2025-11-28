package com.example.detec.network

import com.example.detec.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
// ApiService.kt
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.GET // <--- IMPORTAR
import retrofit2.http.Path // <--- IMPORTAR
interface ApiService {

    @GET("api/reportes/usuario/{id}")
    suspend fun getReportesUsuario(@Path("id") userId: Int): Response<List<ReporteData>>
    // Ruta completa: https://.../api/usuarios/login
    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // Ruta completa: https://.../api/usuarios/register
    @POST("api/usuarios/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>

    @Multipart
    @POST("api/ia/detectar")
    suspend fun analizarImagen(@Part imagen: MultipartBody.Part): Response<PlacaResponse>
    @POST("api/reportes/")
    suspend fun crearReporte(@Body request: ReportRequest): Response<ReportResponse>
}
data class PlacaResponse(
    val message: String?,
    val placa: String?,
    val confianza: Double?
)