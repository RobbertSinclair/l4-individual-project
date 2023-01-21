package com.example.gps_shadow_tracker_app.websocket.operations

import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.websocket.LocationWebSocket
import org.json.JSONObject

class SyncTimeOperation: WebSocketOperation {

    private val webSocket: LocationWebSocket;

    constructor(player: Player, webSocket: LocationWebSocket) : super(player) {
        this.webSocket = webSocket;
    }

    override fun execute(locationJSONObject: JSONObject) {
        this.webSocket.setTime(locationJSONObject.getInt("gameTime"));
    }

}