package com.example.gps_shadow_tracker_app.ui

import android.content.Context
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
    private var player1Marker: Marker?;
    private var player2Marker: Marker?;
    private val context : Context;
    private var coords: LatLng;
    private var changedLocation: Boolean;
    private val cameraPosition: CameraPositionState;
    private val playerMarkerState: MarkerState;
    private var player: Player;

    constructor(context : Context, player: Player) {
        this.context = context;
        //mapFragment.getMapAsync(this);
        this.coords = LatLng(0.0, 0.0);
        this.cameraPosition = CameraPositionState(CameraPosition.fromLatLngZoom(this.coords, 15f))
        this.playerMarkerState = MarkerState(position = this.coords);
        this.player1Marker = null;
        this.player2Marker = null;
        changedLocation = false;
        this.player = player;
        this.gpsShadows = UIGpsShadows(context, Location(LocationManager.GPS_PROVIDER), player);
    }

    /*override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap;
        this.gpsShadows = UIGpsShadows(context, map, Location(LocationManager.GPS_PROVIDER));
        this.coords = LatLng(0.0, 0.0);
        addLocationMarker();
        this.map.moveCamera(CameraUpdateFactory.zoomTo(15.0F));
        if (Constants.IS_RUNNER) {
            this.gpsShadows.getGpsShadows();
        }
    }*/

    override fun updateLocation(location: Location) {
        val newCoords = LatLng(location.latitude, location.longitude);

        if (!newCoords.equals(this.coords)) {
            this.gpsShadows.checkLocationFurtherThanDistance(location);

            this.coords = newCoords;
            moveMarker(this.coords);
            Log.i("NEW_LOCATION_MAP", this.coords.toString())
            //this.player1Marker?.remove();
            //addLocationMarker();
            /*if (player.getPlayerType() == PlayerTypes.CHASER && location.accuracy >= Constants.SHADOW_THRESHOLD) {
                addGPSShadowToMap(location);
            }*/
            if (!changedLocation) {
                this.gpsShadows.getGpsShadows()
                cameraPosition.move(CameraUpdateFactory.newLatLng(this.coords));
                changedLocation = true;
            }
        }
    }

    /*fun updatePlayer2Location(locationObject : JSONObject) {
        var currentLocation = LatLng(
            locationObject.getDouble("latitude"),
            locationObject.getDouble("longitude")
        );
        val shadow = locationObject.getBoolean("inShadow");
        Log.i("SHADOW VALUE", shadow.toString())
        this.player2Marker?.remove();
        if (!shadow) {
            addPlayer2LocationMarker(currentLocation);
        }
    }*/

    fun moveMarker(newCoords: LatLng) {
        playerMarkerState.position = newCoords;
    }

    /*private fun addGPSShadowToMap(location: Location) {
        this.map.addCircle(
            CircleOptions()
                .center(this.coords)
                .radius(Constants.SHADOW_CIRCLE_RADIUS)
                .strokeColor(Color.RED)
                .fillColor(Color.RED)
        )
    }*/


    /*private fun addPlayer2LocationMarker(newLocation: LatLng) {
        this.player2Marker = this.map.addMarker(
            MarkerOptions()
                .position(newLocation)
                .title("Player 2 Location")
        )
        Log.i("PLAYER 2", "Marker Placed")
    }*/

    @Composable
    fun mapView() {
        val location = this.coords
        val locationState = MarkerState(position = location)

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPosition
        ) {
            playerMarker()
            if (player.getPlayerType() == PlayerTypes.RUNNER) {
                gpsShadows.GpsShadows()
            } else {
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

    }



}


