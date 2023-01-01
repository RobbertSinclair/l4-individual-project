package com.example.gps_shadow_tracker_app.websocket.operations

import android.util.Log
import com.example.gps_shadow_tracker_app.game.Player
import org.json.JSONObject

class CaughtOperation: WebSocketOperation {

    constructor(player: Player) : super(player);

    override fun execute(jsonObject: JSONObject) {
        Log.i("CAUGHT", "Caught player");
    }
}