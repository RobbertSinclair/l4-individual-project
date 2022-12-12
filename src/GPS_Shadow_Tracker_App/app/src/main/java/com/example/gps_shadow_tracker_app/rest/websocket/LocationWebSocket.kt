package com.example.gps_shadow_tracker_app.rest.websocket

import android.app.Activity
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.runtime.Composable
import com.example.gps_shadow_tracker_app.Constants
import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.ui.UIMapView
import okhttp3.*
import org.json.JSONObject

class LocationWebSocket : WebSocketListener {

    private val client: OkHttpClient;
    private val request: Request;
    private val webSocket: WebSocket;
    private val mapView: UIMapView;
    private val activity: Activity;
    private val player: Player;

    constructor(context: Context, mapView : UIMapView, player: Player) : super() {
        this.activity = context as Activity;
        this.client = OkHttpClient();
        this.request = Request.Builder().url(Constants.WEBSOCKET_URL).build();
        this.webSocket = this.client.newWebSocket(request, this);
        this.client.dispatcher.executorService.shutdown();
        this.mapView = mapView;
        this.player = player;
    }

    fun sendLocation(locationObject : JSONObject) {
        val accuracy : Float = locationObject.get("accuracy") as Float;
        locationObject.put("type", "LOCATION");
        locationObject.put("inShadow", accuracy >= Constants.SHADOW_THRESHOLD);
        val locationString = locationObject.toString();
        Log.i("LOCATION_STRING", locationString);
        this.webSocket.send(locationString);
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason);
        Log.i("WEBSOCKET_CLOSED", "Reason " + reason);
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response);
        val connectObject = JSONObject();
        connectObject.put("type", "CONNECT");
        this.webSocket.send(connectObject.toString());
        Log.i("WEBSOCKET_CREATED", "Response: " + response.toString());

    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason);
        Log.i("WEBSOCKET_CLOSING", "Reason " + reason);
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response);
        Log.i("WEBSOCKET_FAILED", "Response " + t.toString());
        throw t;
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text);
        var message = JSONObject(text);
        Log.i("WEBSOCKET_MESSAGE", "TEXT: " + text);
        if (message.get("type").equals("LOCATION")) {
            mapView.updatePlayer2Location(message);
        }
        if (message.get("type").equals("CONNECT")) {
            player.setPlayerType(message.getBoolean("chaser"));
            player.setPlayerId(message.getString("id"));

        }
        Log.i("PLAYER_2_LOCATION", "Success on this side");
    }
}