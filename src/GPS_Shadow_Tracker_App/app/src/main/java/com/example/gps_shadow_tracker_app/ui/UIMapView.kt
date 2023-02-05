package com.example.gps_shadow_tracker_app.ui

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.game.PlayerTypes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import org.json.JSONObject

class UIMapView : UILocationWidget {

    private var powerups : UIPowerups;
    private val context : Context;
    private var coords: LatLng;
    private var changedLocation: Boolean;
    private val cameraPosition: CameraPositionState;
    private val playerMarkerState: MarkerState;
    private val player2MarkerState: MarkerState;
    private val otherPlayerMarkers: MutableMap<String, MarkerState>
    private var player: Player;

    constructor(context : Context, player: Player) {
        this.context = context;
        this.coords = LatLng(0.0, 0.0);
        this.cameraPosition = CameraPositionState(CameraPosition.fromLatLngZoom(this.coords, 15f))
        this.playerMarkerState = MarkerState(position = this.coords);
        this.player2MarkerState = MarkerState(position = this.coords)
        this.otherPlayerMarkers = mutableStateMapOf();
        changedLocation = false;
        this.player = player;
        this.powerups = UIPowerups(context, Location(LocationManager.GPS_PROVIDER), player);

    }

    override fun updateLocation(location: Location) {
        Log.i("PLAYER ID", player.getPlayerId().toString());
        val newCoords = LatLng(location.latitude, location.longitude);
        player.setLocation(location);
        if (!newCoords.equals(this.coords)) {
            playerLocationCheck(location);
            this.coords = newCoords;
            moveMarker(playerMarkerState, this.coords);
            Log.i("NEW_LOCATION_MAP", this.coords.toString())
            if (!changedLocation) {
                this.powerups.getGpsShadows()
                cameraPosition.move(CameraUpdateFactory.newLatLng(this.coords));
                changedLocation = true;
            }
        }
    }

    fun playerLocationCheck(location: Location) {
        Log.i("PLAYER_TYPE", player.getPlayerType().toString())
        if (player.getPlayerType() == PlayerTypes.RUNNER) {
            this.powerups.incrementCounter(location);
        } else if (player.getPlayerType() == PlayerTypes.CHASER) {

        }
    }

    fun moveMarker(state: MarkerState, newCoords: LatLng) {
        state.position = newCoords;
    }

    fun updateOtherPlayerLocation(locationObject : JSONObject) {
        try {
            val playerId = locationObject.getString("player");
            var currentLocation = LatLng(
                locationObject.getDouble("latitude"),
                locationObject.getDouble("longitude")
            )
            if (otherPlayerMarkers.containsKey(playerId)) {
                otherPlayerMarkers[playerId]?.let { moveMarker(it, currentLocation) };
            } else {
                otherPlayerMarkers[playerId] = MarkerState(position = currentLocation)
            }

            val shadow = locationObject.getBoolean("inShadow");
            if (!shadow) {
                moveMarker(player2MarkerState, currentLocation);
            }
        } catch (ex : Exception) {
            Log.i("INVALID JSON", "Invalid Location JSON Object received.")
        }

    }

    fun removePlayerMarker(playerId: String) {
        if (otherPlayerMarkers.containsKey(playerId)) {
            otherPlayerMarkers.remove(playerId);
        }
    }

    @Composable
    fun mapView() {
        val playerState = player.getTypeState();

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPosition
        ) {
            playerMarker()

            if (playerState.value == PlayerTypes.RUNNER) {
                //Log.i("GPS_SHADOW_VIEW", "GPS Shadow View Showing")
                //gpsShadows.GpsShadows()

            } else {
                Log.i("OTHER_PLAYERS_VIEW", "Other Players View Showing")
                otherPlayers()
            }
        }
        powerups.chaserButton()
    }

    @Composable
    fun gpsShadows() {
        powerups.GpsShadows()
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
        for (key in otherPlayerMarkers.keys) {
            otherPlayerMarkers[key]?.let {
                Marker(
                    state = it,
                    title = "Player $key Location",
                    icon = BitmapDescriptorFactory.defaultMarker(110F)

                    )
            }
        }

    }

}


