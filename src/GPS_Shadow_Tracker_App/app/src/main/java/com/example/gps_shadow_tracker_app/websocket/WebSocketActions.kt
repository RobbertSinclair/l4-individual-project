package com.example.gps_shadow_tracker_app.websocket

import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.ui.UIMapView
import com.example.gps_shadow_tracker_app.websocket.operations.*
import org.json.JSONObject

enum class WebSocketActions {

    LOCATION {
        override fun setUpOperation(player: Player, mapView: UIMapView, otherPlayers: MutableMap<String, Player>) = LocationOperation(player, mapView, null);
    },
    CAUGHT {
        override fun setUpOperation(player: Player, mapView: UIMapView, otherPlayers: MutableMap<String, Player>) = CaughtOperation(player);
    },
    CONNECT {
        override fun setUpOperation(player: Player, mapView: UIMapView, otherPlayers: MutableMap<String, Player>) = ConnectOperation(player);
    },
    NEW_TYPE {
        override fun setUpOperation(player: Player, mapView: UIMapView, otherPlayers: MutableMap<String, Player>) = NewTypeOperation(player);
    },
    CONNECT_MESSAGE {
        override fun setUpOperation(player: Player, mapView: UIMapView, otherPlayers: MutableMap<String, Player>) = ConnectMessageOperation(player, otherPlayers);
    },
    DISCONNECT {
        override fun setUpOperation(player: Player, mapView: UIMapView, otherPlayers: MutableMap<String, Player>) = DisconnectOperation(player);
    };

    abstract fun setUpOperation(player: Player, mapView: UIMapView, otherPlayers: MutableMap<String, Player>) : WebSocketOperation;

    fun implementAction(player: Player, jsonObject: JSONObject, mapView: UIMapView, otherPlayers: MutableMap<String, Player>) {
        val operation : WebSocketOperation = this.setUpOperation(player, mapView, otherPlayers);
        operation.execute(jsonObject);
    }
}