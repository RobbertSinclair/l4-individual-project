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
import com.example.gps_shadow_tracker_app.ui.UILocationTextViews
import com.example.gps_shadow_tracker_app.ui.UIMapView
import com.example.gps_shadow_tracker_app.ui.accuracyAndPlayerMode
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var gpsService: GPSService;
    private lateinit var locationMap: UIMapView;
    private lateinit var webSocket: LocationWebSocket;
    private lateinit var mainPlayer: Player;
    private lateinit var otherPlayers: MutableList<Player>

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.mainPlayer = Player()
        mainPlayer.setPlayerType(false);
        val textView = UILocationTextViews();
        val mapUI = UIMapView(this, mainPlayer);
        val widgetList = mutableListOf(textView, mapUI);
        gpsService = GPSService(this, widgetList);

        val timer = Timer()


        setContent {
            MaterialTheme {
                mapUI.mapView()
                accuracyAndPlayerMode(textView, mainPlayer)

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



