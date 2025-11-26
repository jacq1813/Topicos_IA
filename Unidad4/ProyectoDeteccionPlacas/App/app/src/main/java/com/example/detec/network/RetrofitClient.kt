package com.example.detec.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // ESTA ES LA URL DE TU PROYECTO EN RENDER
    private const val BASE_URL = "https://webservicesplatesdetectord.onrender.com/"

    private const val BASE_URL_IA = "https://ia-placas-service.onrender.com"

    // Cliente lento (con mucha paciencia) para la IA
    // Como tu IA carga los modelos en cada petición, tardará unos 15-20 segundos.
    private val clientIA = OkHttpClient.Builder()
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // --- INSTANCIA 2: Solo para analizar imágenes ---
    val apiServiceIA: IAService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_IA)
            .client(clientIA) // Usamos el cliente con timeout largo
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IAService::class.java)
    }

}