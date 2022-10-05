package com.example.gps_shadow_tracker_app

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import java.util.*

class MainActivity : AppCompatActivity() {
    private var timer: Timer = Timer();
    private lateinit var latValueLabel : TextView;
    private lateinit var longValueLabel : TextView;
    //private lateinit var permissionLabel : TextView;
    private var counter = 0;
    private var locationPermission : Boolean? = false;
    private lateinit var fusedLocationClient: FusedLocationProviderClient;



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        latValueLabel = findViewById(R.id.latValueLabel);
        locationPermission = getLocationPermission();
        fusedLocationClient = FusedLocationProviderClient(this);
        longValueLabel = findViewById(R.id.longValueLabel);
        longValueLabel.text = counter.toString();
        //permissionLabel.text = "PERMISSIONS PENDING";
        getLocation();

        timer.scheduleAtFixedRate(object: TimerTask() {
            override fun run() {
                runOnUiThread() {
                    getLocation();
                }
            } },0, 1000);


    }

    fun getLocationPermission() : Boolean {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }

    }

    fun getLocation() {

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    this.latValueLabel.text = location.latitude.toString();
                    this.longValueLabel.text = location.longitude.toString();
                }


            }
            
    }
}