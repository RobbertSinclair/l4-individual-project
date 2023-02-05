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
import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.game.PlayerTypes
import com.example.gps_shadow_tracker_app.rest.RestClient
import com.example.gps_shadow_tracker_app.rest.RestInterface
import com.google.maps.android.compose.Circle
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

class UIPowerups: RestInterface {

    private val restClient : RestClient;
    private var location: Location;
    private val counter: MutableState<Int>;
    private var shadows: MutableList<LatLng>;
    private val chaserLocation: MutableState<LatLng>;
    private var player: Player;

    constructor(context : Context, location: Location, player: Player) {
        this.restClient = RestClient(context, this);
        this.location = location;
        this.counter = mutableStateOf(0);
        this.shadows = mutableStateListOf();
        this.player = player;
        this.chaserLocation = mutableStateOf(LatLng(0.0, 0.0))
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

    fun incrementCounter(other: Location) {
        Log.i("DISTANCE", counter.toString());
        /*if (player.getPlayerType() == PlayerTypes.RUNNER && counter % Constants.DISTANCE_THRESHOLD == 0F) {
            location = other;
            //this.getGpsShadows();
        }*/
        this.counter.value++;
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
        TODO("Not yet implemented")
    }

    @Composable
    fun GpsShadows() {
        val shadows = remember { this.shadows }
        shadows.forEach {
            shadow(it)
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
        val progress = remember { this.counter };
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(onClick = {
                Log.i("SEE CHASER","Button Clicked")
            },
                enabled = false
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Powerup: See Chaser")
                    LinearProgressIndicator(progress = progress.value / Constants.DISTANCE_THRESHOLD)
                }

            }
        }
    }

}