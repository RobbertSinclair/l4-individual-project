package com.example.gps_shadow_tracker_app

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.gps_shadow_tracker_app.gps.GPSService
import com.example.gps_shadow_tracker_app.ui.LocationTextViews
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var locationText: LocationTextViews;
    private lateinit var gpsService: GPSService;
    private lateinit var map: GoogleMap;

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        locationText = LocationTextViews(this);
        gpsService = GPSService(this, locationText);
        val mapFragment = supportFragmentManager.findFragmentById(R.id.gpsMap) as SupportMapFragment;
        mapFragment.getMapAsync(this);
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map;
        this.map.addMarker(
            MarkerOptions()
                .position(LatLng(0.0, 0.0))
                .title("Marker")
        )
    }

}