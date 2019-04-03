package com.example.googlemapsgoogleplaces

import android.app.Application
import io.realm.Realm

class HospitalApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}