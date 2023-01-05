package com.example.gps_shadow_tracker_app.websocket.operations

import android.location.Location
import android.util.Log
import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.ui.UIMapView
import com.example.gps_shadow_tracker_app.websocket.LocationWebSocket
import org.json.JSONObject
import java.math.BigDecimal

class LocationOperation:
    WebSocketOperation {

    private val mapView: UIMapView;
    private val otherPlayer: Player?;

    constructor(player: Player, mapView: UIMapView, otherPlayer: Player?, webSocket: LocationWebSocket) : super(player) {
        this.mapView = mapView;
        this.otherPlayer = otherPlayer;
    }

    override fun execute(jsonObject: JSONObject) {
        Log.i("NEW_LOCATION", jsonObject.toString());
        val isChaser = player.isChaser();
        try {
            val otherLocation = Location("")
            otherLocation.latitude = jsonObject.getDouble("latitude");
            otherLocation.longitude = jsonObject.getDouble("longitude")
            otherLocation.accuracy = BigDecimal.valueOf(jsonObject.getDouble("accuracy")).toFloat();
            otherPlayer?.setLocation(otherLocation);
            if (isChaser) {
                val distance = player.getPlayerDistance(otherPlayer);
                if (distance != null && distance < 5) {
                    val caughtObject = JSONObject()
                    caughtObject.put("type", "PLAYER_CAUGHT")
                    caughtObject.put("id", player.getPlayerId())
                    caughtObject.put("caught_id", otherPlayer.getPlayerId())

                }
            }
        } catch (e : Exception) {
            Log.i("OTHER PLAYER FOUND", e.toString())
        }

        mapView.updatePlayer2Location(jsonObject);
    }

}