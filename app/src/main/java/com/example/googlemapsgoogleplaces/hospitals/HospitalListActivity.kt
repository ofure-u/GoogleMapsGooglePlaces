package com.example.googlemapsgoogleplaces.hospitals

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.example.googlemapsgoogleplaces.R
import com.example.googlemapsgoogleplaces.commons.adapter.InfiniteScrollListener
import com.example.googlemapsgoogleplaces.data.HospitalRepository
import com.google.android.gms.location.LocationRequest
import com.patloew.rxlocation.RxLocation
import com.tedpark.tedpermission.rx2.TedRx2Permission
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_hospital_list.*
import java.util.concurrent.TimeUnit

class HospitalListActivity : AppCompatActivity(), HospitalListAdapter.ViewActions {

    lateinit var repository: HospitalRepository
    private var longitude = 0.0
    private var latitude = 0.0
    private var timesPulled = 0
    private val adapter: HospitalListAdapter by lazy {
        HospitalListAdapter(this)
    }
    private val compositeDisposable = CompositeDisposable()
    private val locationRequest = LocationRequest.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_list)

        repository = HospitalRepository.getInstance()

        hospital_list.apply {
            val linearLayout = LinearLayoutManager(context)
            layoutManager = linearLayout
            clearOnScrollListeners()
            addOnScrollListener(
                    InfiniteScrollListener(
                            {
                                loadMore(this@HospitalListActivity.adapter.itemCount)
                            },
                            linearLayout
                    )
            )
        }

        fetchNearbyHospitals()
        refresh.setOnClickListener {
            refreshHospitals()
        }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    private fun loadMore(offset: Int) {
        repository.getHospitalFromApi(
                longitude = longitude,
                latitude = latitude,
                offset = offset
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = {

                        },
                        onSuccess = {
                            adapter.addOrders(it)
                        }
                ).addTo(compositeDisposable)
    }

    @SuppressLint("MissingPermission")
    private fun fetchNearbyHospitals() {
        val rxLocation = RxLocation(this)
        createLocationRequest()
        TedRx2Permission.with(this)
                .setRationaleTitle("Location Permission Required")
                .setRationaleMessage(R.string.permission_rationale_location)
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .request()
                .filter { it.isGranted }
                .flatMapSingle { rxLocation.settings().checkAndHandleResolution(locationRequest) }
                .flatMap { rxLocation.location().updates(locationRequest, 15, TimeUnit.SECONDS) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = {},
                        onNext = {
                            latitude = it.latitude
                            longitude = it.longitude
                            if (timesPulled > 0) return@subscribeBy
                            timesPulled++
                            loadHospitals()
                        }
                ).addTo(compositeDisposable)
    }

    private fun createLocationRequest() {
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 10000
        locationRequest.smallestDisplacement = 50F
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun loadHospitals() {
        repository.loadHospitals(longitude = longitude, latitude = latitude, offset = 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = {

                        },
                        onSuccess = {
                            adapter.clearAndAddOrders(it)
                        }
                ).addTo(compositeDisposable)

    }

    private fun refreshHospitals() {
        repository.forceRefresh()
        loadHospitals()
    }

    override fun onItemSelected(latitude: Double, longitude: Double) {

    }
}
