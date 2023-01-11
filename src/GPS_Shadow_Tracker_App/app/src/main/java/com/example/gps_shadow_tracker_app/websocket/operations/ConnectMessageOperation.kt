package com.example.gps_shadow_tracker_app.websocket.operations

import android.util.Log
import com.example.gps_shadow_tracker_app.game.Player
import org.json.JSONObject

class ConnectMessageOperation: WebSocketOperation {

    constructor(player: Player) : super(player);

    override fun execute(jsonObject: JSONObject) {
        val newPlayer = Player()
        val playerId = jsonObject.getString("player");
        newPlayer.setPlayerId(playerId);
        Log.i("CONNECT_MESSAGE", jsonObject.getString("message"))
    }

}