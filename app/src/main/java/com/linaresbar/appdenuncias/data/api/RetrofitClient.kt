package com.linaresbar.appdenuncias.data.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    const val DEFAULT_BASE_URL = "http://localhost:8090/"

    private var currentBaseUrl: String = DEFAULT_BASE_URL
    private var apiService: DenunciaApiService? = null

    private val gson = GsonBuilder()
        .setLenient()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create()

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    fun getApiService(baseUrl: String = currentBaseUrl): DenunciaApiService {
        val formattedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        if (apiService == null || currentBaseUrl != formattedUrl) {
            currentBaseUrl = formattedUrl
            apiService = Retrofit.Builder()
                .baseUrl(currentBaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(DenunciaApiService::class.java)
        }
        return apiService!!
    }

    fun getBaseUrl(): String = currentBaseUrl

    fun updateBaseUrl(newUrl: String): DenunciaApiService {
        val formattedUrl = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        currentBaseUrl = formattedUrl
        apiService = Retrofit.Builder()
            .baseUrl(currentBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(DenunciaApiService::class.java)
        return apiService!!
    }
}
