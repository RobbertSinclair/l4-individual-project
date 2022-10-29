package com.example.gps_shadow_tracker_app.gps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.gps_shadow_tracker_app.ui.UILocationWidget

class GPSService {

    private var NMEA: String;
    private val locationManager: LocationManager;
    private var gpsListener: GPSListener;
    private val context: Context;

    @RequiresApi(Build.VERSION_CODES.N)
    constructor(context: Context, uiWidgets: List<UILocationWidget>) {
        this.context = context;
        NMEA = "";
        locationManager = this.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager;
        gpsListener = GPSListener(context, uiWidgets);
        if (permissionGranted()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0F, gpsListener);
            locationManager.addNmeaListener(GPSNmeaReader(), Handler());
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
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }



}