package com.example.gps_shadow_tracker_app.websocket

import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.ui.UIMapView
import com.example.gps_shadow_tracker_app.websocket.operations.*
import org.json.JSONObject

enum class WebSocketActions {

    LOCATION {
        override fun setUpOperation(webSocket : LocationWebSocket, player: Player, mapView: UIMapView) = LocationOperation(player, mapView, null, webSocket);
    },
    CAUGHT {
        override fun setUpOperation(webSocket: LocationWebSocket, player: Player, mapView: UIMapView) = CaughtOperation(player);
    },
    CONNECT {
        override fun setUpOperation(webSocket: LocationWebSocket, player: Player, mapView: UIMapView) = ConnectOperation(player);
    },
    NEW_TYPE {
        override fun setUpOperation(webSocket: LocationWebSocket, player: Player, mapView: UIMapView) = NewTypeOperation(player, webSocket);
    },
    CONNECT_MESSAGE {
        override fun setUpOperation(webSocket: LocationWebSocket, player: Player, mapView: UIMapView) = ConnectMessageOperation(player);
    },
    DISCONNECT {
        override fun setUpOperation(webSocket: LocationWebSocket, player: Player, mapView: UIMapView) = DisconnectOperation(player, mapView);
    },
    START_GAME {
        override fun setUpOperation(webSocket: LocationWebSocket, player: Player, mapView: UIMapView) = StartGameOperation(player, webSocket);
    },
    END_GAME {
        override fun setUpOperation(webSocket: LocationWebSocket, player: Player, mapView: UIMapView) = EndGameOperation(player, webSocket);
    },
    SYNC_TIME {
        override fun setUpOperation(webSocket: LocationWebSocket, player: Player, mapView: UIMapView) = SyncTimeOperation(player, webSocket);
    },
    END_JAIL {
        override fun setUpOperation(webSocket: LocationWebSocket, player: Player, mapView: UIMapView) = EndJailOperation(player, webSocket);
    };
    
    abstract fun setUpOperation(webSocket: LocationWebSocket, player: Player, mapView: UIMapView) : WebSocketOperation;

    fun implementAction(webSocket: LocationWebSocket, player: Player, jsonObject: JSONObject, mapView: UIMapView) {
        val operation : WebSocketOperation = this.setUpOperation(webSocket, player, mapView);
        operation.execute(jsonObject);
    }
}