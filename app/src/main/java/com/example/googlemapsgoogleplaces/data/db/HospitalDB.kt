package com.example.googlemapsgoogleplaces.data.db

import com.example.googlemapsgoogleplaces.data.models.Hospital
import com.example.googlemapsgoogleplaces.data.models.HospitalRealm
import io.reactivex.Single
import io.realm.Realm

class HospitalDB {

    fun saveHospital(hospital: Hospital) {
        val realm = Realm.getDefaultInstance()

        realm.beginTransaction()
        val item = realm.where(HospitalRealm::class.java)
                .equalTo("id", hospital.id)
                .findFirst() ?: realm.createObject(HospitalRealm::class.java, hospital.id)

        item.name = hospital.name
        item.phone = hospital.phone
        item.address = hospital.address
        item.latitude = hospital.latitude
        item.longitude = hospital.longitude

        realm.commitTransaction()
        realm.close()
    }

    fun getHospitals(latitude: Double, longitude: Double): Single<List<Hospital>> {
        return Single.create { emitter ->
            val realm = Realm.getDefaultInstance()

            val itemList = realm.where(HospitalRealm::class.java).findAll()

            val hospitals = ArrayList<Hospital>()

            itemList.mapTo(hospitals) { it.convertToHospital() }

            emitter.onSuccess(hospitals)
            realm.close()
        }
    }
}