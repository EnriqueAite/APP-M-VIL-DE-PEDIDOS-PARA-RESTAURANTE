package com.cibertec.elbuensabor.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 10.0.2.2 es la IP especial que usa el EMULADOR para apuntar
    // a "localhost" de tu propia PC. NO uses 127.0.0.1 ni localhost aquí.
    private const val BASE_URL = "http://10.0.2.2:5087/"

    const val IMAGE_BASE_URL = "http://10.0.2.2:5087"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}