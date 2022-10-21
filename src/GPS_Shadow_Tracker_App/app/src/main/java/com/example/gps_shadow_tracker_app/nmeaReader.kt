package com.example.gps_shadow_tracker_app

import android.location.OnNmeaMessageListener
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
class nmeaReader: OnNmeaMessageListener {

    override fun onNmeaMessage(message: String, timestamp: Long) {
        Log.i("NMEA String", message);
    }
}