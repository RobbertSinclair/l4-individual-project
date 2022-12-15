package com.example.gps_shadow_tracker_app.ui

import android.content.Context
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.gps_shadow_tracker_app.Constants
import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.game.PlayerTypes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import org.json.JSONObject
import java.util.*

class UIMapView : UILocationWidget {

    private var gpsShadows : UIGpsShadows;
    private val context : Context;
    private var coords: LatLng;
    private var changedLocation: Boolean;
    private val cameraPosition: CameraPositionState;
    private val playerMarkerState: MarkerState;
    private val player2MarkerState: MarkerState;
    private var player: Player;

    constructor(context : Context, player: Player) {
        this.context = context;
        this.coords = LatLng(0.0, 0.0);
        this.cameraPosition = CameraPositionState(CameraPosition.fromLatLngZoom(this.coords, 15f))
        this.playerMarkerState = MarkerState(position = this.coords);
        this.player2MarkerState = MarkerState(position = this.coords)
        changedLocation = false;
        this.player = player;
        this.gpsShadows = UIGpsShadows(context, Location(LocationManager.GPS_PROVIDER), player);
    }

    override fun updateLocation(location: Location) {
        val newCoords = LatLng(location.latitude, location.longitude);

        if (!newCoords.equals(this.coords)) {
            this.gpsShadows.checkLocationFurtherThanDistance(location);
            this.coords = newCoords;
            moveMarker(playerMarkerState, this.coords);
            Log.i("NEW_LOCATION_MAP", this.coords.toString())
            if (!changedLocation) {
                this.gpsShadows.getGpsShadows()
                cameraPosition.move(CameraUpdateFactory.newLatLng(this.coords));
                changedLocation = true;
            }
        }
    }

    fun moveMarker(state: MarkerState, newCoords: LatLng) {
        state.position = newCoords;
    }

    fun updatePlayer2Location(locationObject : JSONObject) {
        try {
            var currentLocation = LatLng(
                locationObject.getDouble("latitude"),
                locationObject.getDouble("longitude")
            )
            val shadow = locationObject.getBoolean("inShadow");
            if (!shadow) {
                moveMarker(player2MarkerState, currentLocation);
            }
        } catch (ex : Exception) {
            Log.i("INVALID JSON", "Invalid Location JSON Object received.")
        }

    }

    @Composable
    fun mapView() {
        val location = this.coords
        val locationState = MarkerState(position = location)
        val playerState = remember { mutableStateOf(player) }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPosition
        ) {
            playerMarker()
            if (playerState.value.getPlayerType() == PlayerTypes.RUNNER) {
                Log.i("GPS_SHADOW_VIEW", "GPS Shadow View Showing")
                gpsShadows.GpsShadows()
            } else {
                Log.i("OTHER_PLAYERS_VIEW", "Other Players View Showing")
                otherPlayers()
            }
        }
    }

    @Composable
    fun playerMarker() {
        Marker(
            state = playerMarkerState,
            title = "Your Location"
        )
    }

    @Composable
    fun otherPlayers() {
        Marker(
            state = player2MarkerState,
            title = "Player 2 Location",

        )
    }

}


