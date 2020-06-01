package com.iti.mobile.connectedcar

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.LocationSource
import com.iti.mobile.connectedcar.location.LocationClass
import com.iti.mobile.connectedcar.location.LocationPermissionHandler

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity(), LocationInteractor{
    private val PERMISSION_ID = 11
    private val locationPermission by lazy { LocationPermissionHandler(this)}
    private val locationClass by lazy {LocationClass(this)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        if(locationPermission.checkPermissions()){
            Log.d(TAG, "Permission Granted!")
            locationClass.getLastLocation()
        }else{
            Log.d(TAG, "Permission Not Granted!")
            locationPermission.requestPermissions(PERMISSION_ID)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission Granted!!!")
                locationClass.getLastLocation()
            }
        }else {
            locationPermission.requestPermissions(PERMISSION_ID)
        }
    }

    override fun onStop() {
        super.onStop()
        locationClass.cancelLocationUpdate()
    }

    override fun sendCarCurrentSpeed(speed: Float) {

    }

    override fun sendIncreaseTimeFrom10To30(increaseSpeedTime: Long) {
        TODO("Not yet implemented")
    }

    override fun sendDecreaseTimeFrom30To10(decreaseSpeedTime: Long) {
        TODO("Not yet implemented")
    }
}