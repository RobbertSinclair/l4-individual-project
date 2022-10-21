package com.example.gps_shadow_tracker_app

import android.location.Location
import android.location.LocationListener
import android.util.Log

class GPSListener: LocationListener {

    override fun onLocationChanged(location: Location) {
        Log.i("Location Changed", location.toString());
        Log.i("Location Accuracy", location.accuracy.toString());
    }
}