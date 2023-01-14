package com.example.gps_shadow_tracker_app.websocket.operations

import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.websocket.LocationWebSocket
import org.json.JSONObject

class NewTypeOperation: WebSocketOperation {

    private val webSocket: LocationWebSocket;

    constructor(player: Player, webSocket: LocationWebSocket) : super(player) {
        this.webSocket = webSocket;
    };

    override fun execute(jsonObject: JSONObject) {
        player.setPlayerType(jsonObject.getBoolean("chaser"))
        player.setJail(true);
        webSocket.jailTimeService();
    }

}