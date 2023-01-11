package com.example.gps_shadow_tracker_app.websocket.operations

import android.util.Log
import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.ui.UIMapView
import org.json.JSONObject

class DisconnectOperation: WebSocketOperation {

    private val mapView: UIMapView;

    constructor(player: Player, mapView: UIMapView) : super(player) {
        this.mapView = mapView;
    };

    override fun execute(jsonObject: JSONObject) {
        val playerId = jsonObject.getString("player");
        this.mapView.removePlayerMarker(playerId);
        Log.i("DISCONNECT", jsonObject.getString("message"))
    }
}