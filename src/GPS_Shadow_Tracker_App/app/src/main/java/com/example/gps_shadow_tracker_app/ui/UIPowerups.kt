package com.example.gps_shadow_tracker_app.ui

import android.content.Context

import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.gps_shadow_tracker_app.Constants
import com.example.gps_shadow_tracker_app.Constants.Companion.CHASER_LOCATION_URL
import com.example.gps_shadow_tracker_app.Constants.Companion.SECOND
import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.game.PlayerTypes
import com.example.gps_shadow_tracker_app.rest.RestClient
import com.example.gps_shadow_tracker_app.rest.RestInterface
import com.google.maps.android.compose.Circle
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

class UIPowerups: RestInterface {

    private val restClient : RestClient;
    private var location: Location;
    private val counter: MutableState<Int>;
    private var shadows: MutableList<LatLng>;
    private val chaserLocation: MarkerState;
    private var player: Player;
    private val buttonEnabled: MutableState<Boolean>;
    private val chaserShowing: MutableState<Boolean>;
    private val scope: CoroutineScope

    constructor(context : Context, location: Location, player: Player) {
        this.restClient = RestClient(context, this);
        this.location = location;
        this.counter = mutableStateOf(0);
        this.shadows = mutableStateListOf();
        this.buttonEnabled = mutableStateOf(false);
        this.chaserShowing = mutableStateOf(false);
        this.player = player;
        this.chaserLocation = MarkerState(position = LatLng(0.0, 0.0));
        this.scope = CoroutineScope(Dispatchers.Main);
    }

    fun getGpsShadows() {
        if (player.getPlayerType() == PlayerTypes.RUNNER) {
            var locationObject = JSONObject();
            locationObject.put("latitude", this.location.latitude);
            locationObject.put("longitude", this.location.longitude);
            Log.i("GPS_SHADOWS", locationObject.toString());
            this.restClient.post(Constants.LOCATION_SHADOWS_DISTANCE_URL, locationObject);
        }
    }

    fun resetCounter() {
        this.counter.value = 0;
    }

    fun incrementCounter() {
        val accuracy = this.player.getLocation().accuracy;
        val minAccuracy = this.player.getMinAccuracy();
        if (!this.buttonEnabled.value && !this.chaserShowing.value
            && this.counter.value < Constants.POWERUP_THRESHOLD
            && accuracy <= minAccuracy * 2) {
            this.counter.value++;
        } else if (this.counter.value == Constants.POWERUP_THRESHOLD.toInt()) {
            this.buttonEnabled.value = true;
        }
    }

    override fun onPostSuccess(response: JSONObject) {
        val locations : JSONArray = response.get("locations") as JSONArray;
        this.shadows.clear();
        // Get a sample of GPS Shadows
        while (locations.length() > 10 && this.shadows.size < 10) {
            val location = locations.getJSONObject(Random.nextInt(locations.length()));
            val coords = LatLng(location.getDouble("latitude"), location.getDouble("longitude"));
            if (coords !in this.shadows) {
                this.shadows.add(coords);
            }
        }
    }

    override fun onGetFailure() {
        Log.i("ERROR", "The Get Request Failed");
    }

    override fun onPostFailure() {
        Log.i("ERROR", "The Post Request Failed");
    }

    override fun onGetSuccess(response: JSONObject) {
        if (response.has("latitude")) {
            this.scope.launch {
                val newChaserLocation = LatLng(
                    response.getDouble("latitude"),
                    response.getDouble("longitude")
                )
                chaserLocation.position = newChaserLocation;
                chaserShowing.value = true;
                delay(3 * SECOND);
                counter.value = 0;
                chaserShowing.value = false;
                buttonEnabled.value = false;

            }
        }
    }


    fun chaserButtonClicked() {
        this.restClient.get(CHASER_LOCATION_URL)
    }

    @Composable
    fun GpsShadows() {
        val shadows = remember { this.shadows }
        shadows.forEach {
            shadow(it)
        }
    }

    @Composable
    fun chaserLocationDisplay() {
        val locationShowing = remember { this.chaserShowing }
        if (locationShowing.value) {
            Marker(
                state = chaserLocation,
                title = "Chaser Location"
            )
        }
    }

    @Composable
    fun shadow(coords: LatLng) {
        Circle(
            center = coords,
            fillColor = Color.Red,
            strokeColor = Color.Red,
            radius = Constants.SHADOW_CIRCLE_RADIUS
        )
    }

    @Composable
    fun chaserButton() {
        val progress = remember { this.counter }
        val enabled = remember { this.buttonEnabled }
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(onClick = {
                chaserButtonClicked()
            },
                enabled = enabled.value
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Powerup: See Chaser")
                    if (!enabled.value) {
                        LinearProgressIndicator(progress = progress.value / Constants.POWERUP_THRESHOLD)
                    }

                }

            }
        }
    }

}