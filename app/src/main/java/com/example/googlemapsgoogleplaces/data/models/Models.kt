package com.example.googlemapsgoogleplaces.data.models

import com.squareup.moshi.Json
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

data class Hospital(
        val id: String,
        val name: String,
        val address: String,
        val phone: String,
        val latitude: Double,
        val longitude: Double
)

data class RemoteHospital(
        @Json(name = "_id")
        val id: String,
        @Json(name = "address")
        val address: String,
        @Json(name = "name")
        val name: String,
        @Json(name = "phone")
        val phone: String,
        @Json(name = "location")
        val location: Location

) {
    fun convertToHospital(): Hospital {
        var longitude = 0.0
        var latitude = 0.0

        if (location.coordinates.size >= 2) {
            latitude = location.coordinates[0]
            longitude = location.coordinates[1]
        }

        return Hospital(id, name, address, phone, latitude, longitude)
    }

}

data class Location(
        @Json(name = "coordinates")
        val coordinates: List<Double>
)

class HospitalRealm : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var name: String = ""
    var address: String = ""
    var phone: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    fun convertToHospital() = Hospital(id, name, address, phone, latitude, longitude)
}