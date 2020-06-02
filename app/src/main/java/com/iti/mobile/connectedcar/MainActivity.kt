package com.iti.mobile.connectedcar

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.LocationSource
import com.iti.mobile.connectedcar.location.LocationClass
import com.iti.mobile.connectedcar.location.LocationPermissionHandler
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.floor

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity(), LocationInteractor{
    private val PERMISSION_ID = 11
    private val locationPermission by lazy { LocationPermissionHandler(this)}
    private val locationClass by lazy {LocationClass(this, this)}

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
        carSpeed.text = speed.toInt().toString()
    }

    override fun sendIncreaseTimeFrom10To30(increaseSpeedTime: Long) {
        val time = floor(increaseSpeedTime/1000.0).toLong()
        Log.d("WTF", time.toString())
        increaseTime.text = time.toString()
    }

    override fun sendDecreaseTimeFrom30To10(decreaseSpeedTime: Long) {
        val time = floor(decreaseSpeedTime/1000.0).toLong()
        Log.d("WTF", time.toString())
        decreaseTime.text = time.toString()
    }
}