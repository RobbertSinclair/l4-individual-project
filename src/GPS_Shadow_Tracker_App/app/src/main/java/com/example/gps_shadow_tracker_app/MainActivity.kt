package com.example.gps_shadow_tracker_app

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.CancellationTokenSource
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity() {
    private var timer: Timer = Timer();
    private lateinit var latValueLabel : TextView;
    private lateinit var longValueLabel : TextView;
    //private lateinit var permissionLabel : TextView;
    private lateinit var accuracyValueLabel : TextView;
    private var counter = 0;
    private var locationPermission : Boolean? = false;
    private lateinit var fusedLocationClient: FusedLocationProviderClient;
    private val locationUrl = "https://gpsshadows.pythonanywhere.com/submit_location";



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        latValueLabel = findViewById(R.id.latValueLabel);
        locationPermission = getLocationPermission();
        fusedLocationClient = FusedLocationProviderClient(this);
        longValueLabel = findViewById(R.id.longValueLabel);
        longValueLabel.text = counter.toString();
        accuracyValueLabel = findViewById(R.id.accuracyLabelValue);
        accuracyValueLabel.text = "0";
        //permissionLabel.text = "PERMISSIONS PENDING";
        getLocation();

        timer.scheduleAtFixedRate(object: TimerTask() {
            override fun run() {
                runOnUiThread() {
                    getLocation();
                }
            } },0, 2000);



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

        val cancelTokenSource : CancellationTokenSource = CancellationTokenSource()

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
            ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.INTERNET
                ) == PackageManager.PERMISSION_GRANTED){
            fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, cancelTokenSource.token)
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        this.latValueLabel.text = location.latitude.toString();
                        this.longValueLabel.text = location.longitude.toString();
                        this.accuracyValueLabel.text = location.accuracy.toString();
                        var locationParams = HashMap<String, String>();
                        locationParams["latitude"] = location.latitude.toString();
                        locationParams["longitude"] = location.longitude.toString();
                        locationParams["accuracy"] = location.accuracy.toString();
                        val jsonObject = JSONObject(locationParams as Map<*, *>?);
                        val queue = Volley.newRequestQueue(this);
                        val stringRequest = JsonObjectRequest(Request.Method.POST, locationUrl, jsonObject,
                        Response.Listener { response ->
                            Log.i("Location", response.toString());
                        }, Response.ErrorListener {
                            Log.i("Location", "Error");
                            }
                            );

                        queue.add(stringRequest);



                    }



                }
        }


    }
}