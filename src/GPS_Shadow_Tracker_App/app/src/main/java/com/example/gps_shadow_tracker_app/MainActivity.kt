package com.example.gps_shadow_tracker_app

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.gps.GPSService
import com.example.gps_shadow_tracker_app.rest.websocket.LocationWebSocket
import com.example.gps_shadow_tracker_app.ui.UIMapView
import com.example.gps_shadow_tracker_app.ui.accuracyAndPlayerMode
import com.example.gps_shadow_tracker_app.ui.mapView

class MainActivity : AppCompatActivity() {
    private lateinit var gpsService: GPSService;
    private lateinit var locationMap: UIMapView;
    private lateinit var webSocket: LocationWebSocket;
    private lateinit var player: Player;

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                mapView()
                accuracyAndPlayerMode(5.0, true)

            }
        }
        /*setContentView(R.layout.activity_main);

        val mapFragment = supportFragmentManager.findFragmentById(R.id.gpsMap) as SupportMapFragment;
        this.player = Player();
        locationMap = UIMapView(this, mapFragment, player);
        webSocket = LocationWebSocket(this, locationMap, player);
        val widgetList = mutableListOf(locationMap);
        gpsService = GPSService(this, widgetList, webSocket);*/
    }


}



