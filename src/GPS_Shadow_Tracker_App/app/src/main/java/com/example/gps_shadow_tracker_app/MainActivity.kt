package com.example.gps_shadow_tracker_app

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.game.PlayerTypes
import com.example.gps_shadow_tracker_app.gps.GPSService
import com.example.gps_shadow_tracker_app.rest.websocket.LocationWebSocket
import com.example.gps_shadow_tracker_app.ui.UILocationTextViews
import com.example.gps_shadow_tracker_app.ui.UIMapView
import com.google.android.gms.maps.SupportMapFragment
import com.google.maps.android.compose.GoogleMap

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

@Composable
fun chaserOrRunner(chaser: Boolean) {
    if (chaser) {
        bigText("Chaser")
    } else {
        bigText("Runner")
    }
}

@Composable
fun bigText(text: String) {
    Text(text, fontSize=25.sp)
}

@Composable
fun accuracyLabel(accuracy: Double) {
    bigText("Error: $accuracy meters");
}

@Composable
fun accuracyAndPlayerMode(accuracy: Double, chaser: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        accuracyLabel(accuracy)
        chaserOrRunner(chaser)
    }
    Spacer(modifier=Modifier.height(8.dp))


}



@Preview
@Composable
fun previewAccuracyAndPlayerMode() {
    MaterialTheme {
        accuracyAndPlayerMode(5.0, true);
    }
}