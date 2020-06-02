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
import com.iti.mobile.connectedcar.LocationInteractor
import java.lang.ref.WeakReference
import java.util.*


class LocationClass(context: Context, private var listener: LocationInteractor?) {

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
            Log.d("Location2", mLastLocation?.speed.toString())
            Log.d("Location2", mLastLocation?.latitude.toString())
            Log.d("Location2", mLastLocation?.longitude.toString())
            if(mLastLocation!= null && mLastLocation.speed >= 1){
                val speed = mLastLocation.speed * 3.6f
                calculateTime(speed.toInt())
                Log.d("Location1", speed.toString())
                listener?.sendCarCurrentSpeed(speed)
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
                        Log.d("Location2", location.speed.toString())
                        Log.d("Location2", location.latitude.toString())
                        Log.d("Location2", location.longitude.toString())
                        if(location.speed >= 1.0){
                            val speed = location.speed * 3.6f
                            calculateTime(speed.toInt())
                            Log.d("Location1", speed.toString())
                            listener?.sendCarCurrentSpeed(speed)
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
        mLocationRequest.interval = 0
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
        listener = null
    }

    fun calculateTime(speed: Int){
        if(speed == 10){
            //1
            increaseSpeedStartTime = System.currentTimeMillis()
            //2
            decreaseSpeedEndTime = System.currentTimeMillis()
            if(decreaseSpeedStartTime != 0L) {
                val decreaseTime = decreaseSpeedEndTime - decreaseSpeedStartTime
                listener?.sendDecreaseTimeFrom30To10(decreaseTime)
                decreaseSpeedStartTime = 0L
            }
        }else if (speed == 30){
            //1
            if(increaseSpeedStartTime != 0L) {
                increaseSpeedEndTime = System.currentTimeMillis()
                val increaseTime = increaseSpeedEndTime - increaseSpeedStartTime
                listener?.sendIncreaseTimeFrom10To30(increaseTime)
                increaseSpeedStartTime = 0L
            }
            //2
            decreaseSpeedStartTime = System.currentTimeMillis()
        }
    }
}