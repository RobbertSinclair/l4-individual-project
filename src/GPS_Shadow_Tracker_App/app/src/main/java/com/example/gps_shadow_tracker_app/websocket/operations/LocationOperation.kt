package com.example.gps_shadow_tracker_app.websocket.operations

import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.ui.UIMapView
import org.json.JSONObject

class LocationOperation:
    WebSocketOperation {

    private val mapView: UIMapView;

    constructor(player: Player, mapView: UIMapView) : super(player) {
        this.mapView = mapView;
    };

    override fun execute(jsonObject: JSONObject) {
        mapView.updatePlayer2Location(jsonObject);
    }

}