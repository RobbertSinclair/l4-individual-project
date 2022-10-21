package com.example.gps_shadow_tracker_app

import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.location.OnNmeaMessageListener
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi

class GPSLocation {

    private var NMEA: String;
    private val locationManager: LocationManager;
    private var gpsListener: GPSListener;

    @RequiresApi(Build.VERSION_CODES.N)
    constructor(context: Context) {
        NMEA = "";
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager;
        gpsListener = GPSListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0F, gpsListener);
        locationManager.addNmeaListener(nmeaReader(), Handler());
    }


}