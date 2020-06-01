package com.iti.mobile.connectedcar.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import java.lang.ref.WeakReference


class LocationPermissionHandler(context: Context){

    private var context: WeakReference<Context> = WeakReference(context)

    fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
                context.get()!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context.get()!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(permissionID: Int) {
        ActivityCompat.requestPermissions(
            (context.get() as Activity),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionID
        )
    }
}