package com.example.detec.model

import com.google.gson.annotations.SerializedName

// --- MODELOS DE USUARIO ---

data class LoginRequest(
    @SerializedName("Correo") val correo: String,
    @SerializedName("Contrasena") val contrasena: String
)

data class RegisterRequest(
    @SerializedName("Nombre") val nombre: String,
    @SerializedName("Correo") val correo: String,
    @SerializedName("Contrasena") val contrasena: String
)
data class PlacaResponse(
    val placa: String?,
    val confianza: Double?,
    val error: String?
)
data class LoginResponse(
    val message: String?, // "Login exitoso"
    val error: String?,   // Por si falla
    @SerializedName("Usuario") val usuario: UsuarioData?
)

data class UsuarioData(
    @SerializedName("UsuarioID") val id: Int,
    @SerializedName("Nombre") val nombre: String,
    @SerializedName("Correo") val correo: String
)

// --- MODELOS DE REPORTE ---

data class ReportRequest(
    @SerializedName("UsuarioID") val usuarioId: Int,
    @SerializedName("NumPlaca") val numPlaca: String,
    @SerializedName("Descripcion") val descripcion: String,
    @SerializedName("Coordenadas") val coordenadas: String,
    @SerializedName("ImgEvidencia") val imgEvidencia: String? = null // Opcional por ahora
)

data class ReportResponse(
    val message: String?,
    val error: String?
)