package ru.climatlab.service.data.backend

import com.google.gson.GsonBuilder
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ClimatLabApiClient {

    val climatLabService: ClimatLabApiService by lazy {

        val okHttp = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).apply { level = HttpLoggingInterceptor.Level.BODY })
                .addInterceptor(AuthInterceptor())
                .build()


        val retrofitBuilder = Retrofit.Builder()
                .addConverterFactory(
                        GsonConverterFactory.create(
                                GsonBuilder()
                                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                                        .create()
                        )
                )
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(okHttp)

        retrofitBuilder
                .baseUrl("https://crm-laclimat.ru")
                .build()
                .create(ClimatLabApiService::class.java)
    }
}
