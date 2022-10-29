package com.example.gps_shadow_tracker_app

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.gps_shadow_tracker_app.gps.GPSService
import com.example.gps_shadow_tracker_app.ui.UILocationTextViews
import com.example.gps_shadow_tracker_app.ui.UIMapView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity() {
    private lateinit var locationText: UILocationTextViews;
    private lateinit var gpsService: GPSService;
    private lateinit var locationMap: UIMapView;

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        val mapFragment = supportFragmentManager.findFragmentById(R.id.gpsMap) as SupportMapFragment;
        locationText = UILocationTextViews(this);
        locationMap = UIMapView(this, mapFragment);
        val widgetList = mutableListOf(locationText, locationMap);
        gpsService = GPSService(this, widgetList);
    }

}