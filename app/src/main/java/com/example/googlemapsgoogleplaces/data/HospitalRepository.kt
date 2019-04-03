package com.example.googlemapsgoogleplaces.data

import com.example.googlemapsgoogleplaces.data.api.ApiService
import com.example.googlemapsgoogleplaces.data.db.HospitalDB
import com.example.googlemapsgoogleplaces.data.models.Hospital
import io.reactivex.Single

class HospitalRepository(private val api: ApiService,
                         private val db: HospitalDB) {
    private var refresh = false

    fun forceRefresh() {
        refresh = true
    }

    fun loadHospitals(longitude: Double, latitude: Double, offset: Int): Single<List<Hospital>> {
        val remoteSource = getHospitalFromApi(longitude, latitude, offset)

        if (refresh) {
            refresh = false
            return remoteSource
        }
        val localSource = getHospitalFromDb(longitude, latitude, offset)
        return Single.concat(localSource, remoteSource)
                .filter { it.isNotEmpty() }
                .first(emptyList())
    }

    fun getHospitalFromDb(longitude: Double, latitude: Double, offset: Int): Single<List<Hospital>> {
        return db.getHospitals(longitude = longitude, latitude = latitude)
    }

    fun getHospitalFromApi(longitude: Double, latitude: Double, offset: Int): Single<List<Hospital>> {
        return api.getHospitals(latitude = latitude, longitude = longitude, offset = offset)
                .map { hospitals ->
                    val list = ArrayList<Hospital>()
                    hospitals.mapTo(list) { it.convertToHospital() }
                    list
                }.flatMap { hospitals ->
                    for (hospital in hospitals) db.saveHospital(hospital)
                    Single.just(hospitals)
                }
    }

    companion object {
        fun getInstance(): HospitalRepository {
            val apiService = ApiService.getInstance()
            return HospitalRepository(
                    apiService,
                    HospitalDB()
            )
        }
    }
}