package com.example.capstoneproject.API

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null
    private var host = "http://192.168.123.104:8080";
    private val gson = GsonBuilder().setLenient().create()

    private fun getInstance(): Retrofit {
        if (retrofit == null) {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(host)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit!!
    }

    val apiService: ApiService by lazy {
        getInstance().create(ApiService::class.java)
    }

    fun createApiServiceWithAuth(context: Context): ApiService {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)


        val tokenInterceptor = Interceptor { chain ->
            val sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("auth_token", null)
            val request = chain.request().newBuilder()
            token?.let {
                request.addHeader("Authorization", token)
            }
            chain.proceed(request.build())
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(tokenInterceptor)
            .build()

        val retrofitWithAuth = Retrofit.Builder()
            .baseUrl(host)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofitWithAuth.create(ApiService::class.java)
    }
}
