package com.example.gps_shadow_tracker_app.websocket.operations

import com.example.gps_shadow_tracker_app.game.Player
import org.json.JSONObject

class NewTypeOperation: WebSocketOperation {

    constructor(player: Player) : super(player);

    override fun execute(jsonObject: JSONObject) {
        player.setPlayerType(jsonObject.getBoolean("chaser"))
    }

}