package com.example.gps_shadow_tracker_app.websocket.operations

import android.util.Log
import com.example.gps_shadow_tracker_app.game.Player
import org.json.JSONObject

open abstract class WebSocketOperation {

    open val player: Player;

    constructor(player: Player) {
        this.player = player;
    }

    open fun execute(locationJSONObject: JSONObject) {
        Log.i("EXECUTE", "Execution")
    }

}