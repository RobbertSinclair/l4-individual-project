package com.example.gps_shadow_tracker_app

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.game.PlayerTypes
import com.example.gps_shadow_tracker_app.gps.GPSService
import com.example.gps_shadow_tracker_app.websocket.LocationWebSocket
import com.example.gps_shadow_tracker_app.ui.UILocationTextViews
import com.example.gps_shadow_tracker_app.ui.UIMapView
import com.example.gps_shadow_tracker_app.ui.accuracyAndPlayerMode
import com.example.gps_shadow_tracker_app.ui.bigText
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var gpsService: GPSService;
    private lateinit var locationMap: UIMapView;
    private lateinit var webSocket: LocationWebSocket;
    private lateinit var mainPlayer: Player;

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.mainPlayer = Player()
        mainPlayer.setPlayerType(PlayerTypes.CHASER);
        val textView = UILocationTextViews();
        locationMap = UIMapView(this, mainPlayer);
        webSocket = LocationWebSocket(this, locationMap, mainPlayer);
        val widgetList = mutableListOf(textView, locationMap);
        gpsService = GPSService(this, widgetList, webSocket);
        setContent {
            MaterialTheme {
                locationMap.mapView()
                Column() {
                    accuracyAndPlayerMode(textView, mainPlayer, webSocket)
                    webSocket.notificationCenter()
                }
            }
        }
    }


}



