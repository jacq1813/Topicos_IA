package com.example.detec.network

import com.example.detec.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    // --- USUARIOS ---

    // Ruta completa: https://.../api/usuarios/login
    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // Ruta completa: https://.../api/usuarios/register
    @POST("api/usuarios/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>


    // --- REPORTES ---

    // Ruta completa: https://.../api/reportes/
    @POST("api/reportes/")
    suspend fun crearReporte(@Body request: ReportRequest): Response<ReportResponse>
}