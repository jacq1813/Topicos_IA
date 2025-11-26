package com.example.detec.network

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import com.example.detec.model.PlacaResponse // Asegúrate de tener este modelo (ver abajo)

interface IAService {
    @Multipart
    @POST("analizar") // Esta ruta debe coincidir con la de tu app.py (@app.route('/analizar'))
    suspend fun analizarPlaca(@Part imagen: MultipartBody.Part): Response<PlacaResponse>
}