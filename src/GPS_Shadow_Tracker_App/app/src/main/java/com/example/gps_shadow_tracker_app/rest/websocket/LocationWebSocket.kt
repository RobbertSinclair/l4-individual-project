package com.example.gps_shadow_tracker_app.rest.websocket

import android.location.Location
import android.util.Log
import com.example.gps_shadow_tracker_app.Constants
import okhttp3.*
import org.json.JSONObject

class LocationWebSocket : WebSocketListener {

    private val client: OkHttpClient;
    private val request: Request;
    private val webSocket: WebSocket;

    constructor() : super() {
        this.client = OkHttpClient();
        this.request = Request.Builder().url(Constants.WEBSOCKET_URL).build();
        this.webSocket = this.client.newWebSocket(request, this);
        this.client.dispatcher.executorService.shutdown();
    }

    fun sendLocation(location : Location) {
        var locationObject = JSONObject();
        locationObject.put("latitude", location.latitude);
        locationObject.put("longitude", location.longitude);
        locationObject.put("inShadow", location.accuracy >= Constants.SHADOW_THRESHOLD);
        val locationString = locationObject.toString();
        this.webSocket.send(locationString);
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason);
        Log.i("WEBSOCKET_CLOSED", "Reason " + reason);
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response);
        Log.i("WEBSOCKET_CREATED", "Response: " + response.toString());
        webSocket.send("Ping");
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason);
        Log.i("WEBSOCKET_CLOSING", "Reason " + reason);
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response);
        Log.i("WEBSOCKET_FAILED", "Response " + response.toString());
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text);
        Log.i("WEBSOCKET_MESSAGE", "TEXT: " + text);
    }

}