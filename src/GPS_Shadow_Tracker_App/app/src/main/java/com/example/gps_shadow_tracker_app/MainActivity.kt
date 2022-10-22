package com.example.gps_shadow_tracker_app

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.gps_shadow_tracker_app.gps.GPSService
import com.example.gps_shadow_tracker_app.ui.LocationTextViews

class MainActivity : AppCompatActivity() {
    private lateinit var locationText: LocationTextViews;
    private lateinit var gpsService: GPSService;

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        locationText = LocationTextViews(this);
        gpsService = GPSService(this, locationText);
    }

}