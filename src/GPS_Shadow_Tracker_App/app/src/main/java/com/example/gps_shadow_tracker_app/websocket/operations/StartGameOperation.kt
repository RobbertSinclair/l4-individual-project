package com.example.gps_shadow_tracker_app.websocket.operations

import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.ui.UIMapView
import com.example.gps_shadow_tracker_app.websocket.LocationWebSocket
import org.json.JSONObject

class StartGameOperation: WebSocketOperation {

    private val webSocket : LocationWebSocket;
    private val mapView: UIMapView;

    constructor(player: Player, webSocket: LocationWebSocket, mapView: UIMapView) : super(player) {
        this.webSocket = webSocket;
        this.mapView = mapView;
    }

    override fun execute(jsonObject: JSONObject) {
        webSocket.setTime(jsonObject.getInt("gameTime"));
        mapView.startGame()
        webSocket.startGame();
    }
}