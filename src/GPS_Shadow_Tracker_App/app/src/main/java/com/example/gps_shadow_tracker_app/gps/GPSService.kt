package com.example.gps_shadow_tracker_app.gps

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.example.gps_shadow_tracker_app.Constants
import com.example.gps_shadow_tracker_app.websocket.LocationWebSocket
import com.example.gps_shadow_tracker_app.ui.UILocationWidget

class GPSService {

    private var NMEA: String;
    private val locationManager: LocationManager;
    private var gpsListener: GPSListener;
    private val context: Context;
    private val catchRadius: GPSCatchRadius;

    @RequiresApi(Build.VERSION_CODES.N)
    constructor(context: Context, uiWidgets: List<UILocationWidget>, webSocket: LocationWebSocket) {
        this.context = context;
        NMEA = "";
        locationManager = this.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager;
        gpsListener = GPSListener(context, uiWidgets, webSocket);
        catchRadius = GPSCatchRadius(gpsListener);
        var permission = permissionGranted();
        startLocations();
    }


    fun startLocations() {
        var permission = permissionGranted()
        if (permission) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constants.LOCATION_DELAY, Constants.MIN_DISTANCE, gpsListener);
            locationManager.registerGnssStatusCallback(catchRadius, null);
        } else {
            Log.i("PERMISSION DENIED", "You must get location permission")
            requestPermissions(this.context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 1)
            startLocations()

        }
    }

    fun permissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }



}