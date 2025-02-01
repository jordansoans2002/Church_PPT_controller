package com.example.church_ppt_controller.network

import com.example.church_ppt_controller.utils.DynamicBaseUrlInterceptor
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val PORT = 5000
object RetrofitClient {
    private val baseInterceptor = DynamicBaseUrlInterceptor()
    private val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private var currentBaseUrl: String? = null

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(baseInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.38") // Default URL, will be overridden by interceptor
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun updateBaseUrl(newUrl: String) {
        if(newUrl != currentBaseUrl){
            newUrl.toHttpUrlOrNull() ?: throw IllegalArgumentException("Invalid URL format")
            currentBaseUrl = newUrl
            baseInterceptor.updateBaseUrl(newUrl)
        }
    }

    fun getControllerApiService(): ControllerApiService = retrofit
        .create(ControllerApiService::class.java)

    fun getCreatorApiService(): CreatorApiService = retrofit
        .create(CreatorApiService::class.java)

}
