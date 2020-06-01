package com.iti.mobile.connectedcar.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.provider.Settings
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.*
import java.lang.ref.WeakReference
import java.util.*


class LocationClass(context: Context) {

    private val mFusedLocationClient: FusedLocationProviderClient by lazy{ LocationServices.getFusedLocationProviderClient(context)}
    private val context: WeakReference<Context> by lazy{ WeakReference(context)}
    private var visited = false
    private var increaseSpeedStartTime = 0L
    private var increaseSpeedEndTime = 0L
    private var decreaseSpeedStartTime = 0L
    private var decreaseSpeedEndTime = 0L

    private var mLocationCallback: LocationCallback? = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            val mLastLocation: Location? = locationResult?.lastLocation
            if(mLastLocation!= null && mLastLocation.speed >= 1){
                calculateTime(mLastLocation.speed)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        if (isLocationEnabled()) {
            if(!visited) {
                requestNewLocationData()
                visited = true
            }
            mFusedLocationClient.lastLocation.addOnCompleteListener{ task ->
                    val location: Location? = task.result
                    if (location == null ) {
                        if(!visited)
                            requestNewLocationData()
                    } else {
                        if(location.speed >= 1.0){
                            calculateTime(location.speed)
                        }
                    }
                }

        } else {
            visited = false
            Toast.makeText(context.get(), "Turn on location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.get()!!.startActivity(intent)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest.create()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 2000
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.getMainLooper()
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context.get()!!
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return Objects.requireNonNull(locationManager)
            .isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    fun cancelLocationUpdate(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }

    fun calculateTime(speed: Float){
        if((10.0f - speed) in 0.0..1.0){
            //1
            increaseSpeedStartTime = System.currentTimeMillis()
            //2
            decreaseSpeedEndTime = System.currentTimeMillis()
            val decreaseTime = decreaseSpeedEndTime - decreaseSpeedStartTime
            //TODO: send to mainActivity
            //..
        }else if ((30.0f - speed) in 0.0..1.0){
            //1
            increaseSpeedEndTime = System.currentTimeMillis()
            val increaseTime = increaseSpeedEndTime - increaseSpeedStartTime
            // TODO: send to mainActivity.
            //..
            //2
            decreaseSpeedStartTime = System.currentTimeMillis()
        }
    }
}