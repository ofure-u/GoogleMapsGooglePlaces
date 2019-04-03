package com.example.googlemapsgoogleplaces.data.api

import com.example.googlemapsgoogleplaces.data.models.RemoteHospital
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    companion object {
        const val baseUrl = "https://peaceful-cove-91231.herokuapp.com"
    }

    @GET("api/hospitals")
    fun getHospitals(@Query("latitude") latitude: Double,
                     @Query("longitude") longitude: Double): Single<List<RemoteHospital>>

}
