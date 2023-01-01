package com.example.gps_shadow_tracker_app.websocket

import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.ui.UIMapView
import com.example.gps_shadow_tracker_app.websocket.operations.*
import org.json.JSONObject

enum class WebSocketActions {

    LOCATION {
        override fun setUpOperation(player: Player, mapView: UIMapView) = LocationOperation(player, mapView);
    },
    CAUGHT {
        override fun setUpOperation(player: Player, mapView: UIMapView) = CaughtOperation(player);
    },
    CONNECT {
        override fun setUpOperation(player: Player, mapView: UIMapView) = ConnectOperation(player);
    },
    NEW_TYPE {
        override fun setUpOperation(player: Player, mapView: UIMapView) = NewTypeOperation(player);
    },
    CONNECT_MESSAGE {
        override fun setUpOperation(player: Player, mapView: UIMapView) = ConnectMessageOperation(player);
    },
    DISCONNECT {
        override fun setUpOperation(player: Player, mapView: UIMapView) = DisconnectOperation(player);
    };

    abstract fun setUpOperation(player: Player, mapView: UIMapView) : WebSocketOperation;

    fun implementAction(player: Player, jsonObject: JSONObject, mapView: UIMapView) {
        val operation : WebSocketOperation = this.setUpOperation(player, mapView);
        operation.execute(jsonObject);
    }
}