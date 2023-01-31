package com.example.gps_shadow_tracker_app.game

import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.gps_shadow_tracker_app.ui.UIMapView
import com.example.gps_shadow_tracker_app.ui.bigText
import org.json.JSONObject

open class Player {

    private var type: PlayerTypes?;
    private var typeState : MutableState<PlayerTypes?>;
    private var id : String?;
    private var location: Location;
    private var inShadow: Boolean;
    private var jail: Boolean;
    private var satelliteNoiseRatio: Float;

    constructor() {
        this.type = PlayerTypes.RUNNER;
        this.typeState = mutableStateOf(this.type);
        this.id = null;
        this.location = Location(LocationManager.GPS_PROVIDER);
        this.inShadow = false;
        this.jail = false;
        this.satelliteNoiseRatio = 0F;
    }

    fun getPlayerDistance(other: Player?): Float? {
        val otherPlayerLocation = other?.getLocation();
        if (otherPlayerLocation != null) {
            return location.distanceTo(otherPlayerLocation);
        } else {
            return null;
        }
    }

    fun getPlayerType() : PlayerTypes? {
        return this.type;
    }

    fun isChaser() : Boolean {
        return this.type == PlayerTypes.CHASER
    }

    fun setPlayerType(type: PlayerTypes) {
        this.type = type;
        typeState.value = this.type;
        Log.i("PLAYER Type State", "Type state is ${typeState.value.toString()}")
    }

    fun setPlayerType(type: Boolean) {
        if (type) {
            this.type = PlayerTypes.CHASER
        } else {
            this.type = PlayerTypes.RUNNER
        }
        typeState.value = this.type;
        Log.i("PLAYER Type State", "Type state is ${typeState.value.toString()}")
    }

    fun setPlayerId(id : String) {
        this.id = id;
    }

    fun setLocation(location: Location) {
        this.location = location;
    }

    fun getLocation(): Location {
        return this.location;
    }

    fun getTypeState() : MutableState<PlayerTypes?> {
        return this.typeState;
    }

    fun setJail(newJail: Boolean) {
        this.jail = newJail;
    }

    fun setSatelliteRatio(noiseRatio: Float) {
        this.satelliteNoiseRatio = noiseRatio;
    }

    fun getSatelliteRatio() : Float {
        return this.satelliteNoiseRatio;
    }

    fun inJail() : Boolean {
        return jail;
    }

    fun checkDistance(other: Player) {
        val otherLocation = other.getLocation();
        if (otherLocation != null) {
            val distance = this.location?.distanceTo(other.getLocation() as Location);
            Log.i("PLAYER_DISTANCE", distance.toString());
        }
    }

    fun getPlayerId() : String? {
        return this.id;
    }

    @Composable
    fun chaserOrRunner() {
        val state = typeState
        if (state.value == PlayerTypes.CHASER) {
            bigText("Chaser")
        } else if (state.value == PlayerTypes.RUNNER) {
            bigText("Runner")
        } else {
            bigText("Getting Role")
        }
    }

}

