package com.example.googlemapsgoogleplaces.data.api

import com.example.googlemapsgoogleplaces.BuildConfig
import com.example.googlemapsgoogleplaces.data.models.RemoteHospital
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface ApiService {
    companion object {
        const val baseUrl = "https://peaceful-cove-91231.herokuapp.com"

        fun getInstance(): ApiService {
            val httpClient = OkHttpClient.Builder()
                    .readTimeout(120, TimeUnit.SECONDS)
                    .connectTimeout(120, TimeUnit.SECONDS)

            httpClient.addInterceptor { chain ->
                val request =
                        chain.request().newBuilder().addHeader("AppVersion", BuildConfig.VERSION_NAME)
                                .build()
                chain.proceed(request)
            }

            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY

                httpClient.addInterceptor(logging)
            }

            return Retrofit.Builder()
                    .baseUrl(ApiService.baseUrl)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create())
                    .client(httpClient.build())
                    .build()
                    .create(ApiService::class.java)
        }
    }

    @GET("api/hospitals")
    fun getHospitals(@Query("offset") offset: Int,
                     @Query("latitude") latitude: Double,
                     @Query("longitude") longitude: Double): Single<List<RemoteHospital>>

}
