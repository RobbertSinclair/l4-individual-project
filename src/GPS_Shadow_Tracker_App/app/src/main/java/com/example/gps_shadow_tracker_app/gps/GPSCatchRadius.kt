package com.example.gps_shadow_tracker_app.gps

import android.location.GnssStatus
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
class GPSCatchRadius: GnssStatus.Callback {

    private val listener: GPSListener

    constructor(listener: GPSListener) : super() {
        this.listener = listener;
    };

    override fun onSatelliteStatusChanged(status: GnssStatus) {
        val satellites = status.satelliteCount;
        var totalRatio: Float = 0F;
        for (i in 0 until satellites) {
            // Cn0DbHz gets the ratio
            totalRatio += status.getCn0DbHz(i);
        }
        val averageRatio = (totalRatio / satellites) / 64;
        listener.setNoiseRatio(averageRatio);
    }



}