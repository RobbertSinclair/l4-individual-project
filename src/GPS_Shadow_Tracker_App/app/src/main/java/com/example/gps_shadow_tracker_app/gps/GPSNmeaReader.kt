package com.example.gps_shadow_tracker_app.gps

import android.location.OnNmeaMessageListener
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
class GPSNmeaReader: OnNmeaMessageListener {

    override fun onNmeaMessage(message: String, timestamp: Long) {
        Log.i("NMEA String", message);
    }
}