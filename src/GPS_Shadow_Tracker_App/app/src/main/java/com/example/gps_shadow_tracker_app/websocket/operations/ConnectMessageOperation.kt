package com.example.gps_shadow_tracker_app.websocket.operations

import android.util.Log
import com.example.gps_shadow_tracker_app.game.Player
import org.json.JSONObject

class ConnectMessageOperation: WebSocketOperation {

    private var otherPlayers: MutableMap<String, Player>;

    constructor(player: Player, otherPlayers: MutableMap<String, Player>) : super(player) {
        this.otherPlayers = otherPlayers
    };

    override fun execute(jsonObject: JSONObject) {
        val newPlayer = Player()
        val playerId = jsonObject.getString("player");
        newPlayer.setPlayerId(playerId);
        this.otherPlayers[playerId] = newPlayer
        Log.i("CONNECT_MESSAGE", jsonObject.getString("message"))
    }

}