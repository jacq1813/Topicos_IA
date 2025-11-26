package com.example.detec.network

import com.example.detec.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // 1. LOGIN
    @POST("/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // 2. REGISTRO
    @POST("/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>

    // 3. ENVIAR FOTO PARA DETECTAR PLACA (Multipart)
    // Esto enviará la foto al servidor, el servidor corre el modelo y devuelve el texto de la placa
    @Multipart
    @POST("/detectar-placa")
    suspend fun detectPlate(
        @Part image: MultipartBody.Part
    ): Response<PlateRecognitionResponse>

    // 4. GUARDAR REPORTE FINAL
    @POST("/crear-reporte")
    suspend fun createReport(@Body request: ReportRequest): Response<LoginResponse>
}