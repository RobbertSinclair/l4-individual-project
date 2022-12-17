package com.example.gps_shadow_tracker_app.websocket.operations

import org.json.JSONObject
import com.example.gps_shadow_tracker_app.game.Player;

class ConnectOperation:
    WebSocketOperation {

    constructor(player: Player) : super(player);

    override fun execute(jsonObject: JSONObject) {
        this.player.setPlayerType(jsonObject.getBoolean("chaser"));
        this.player.setPlayerId(jsonObject.getString("id"));
    }
}