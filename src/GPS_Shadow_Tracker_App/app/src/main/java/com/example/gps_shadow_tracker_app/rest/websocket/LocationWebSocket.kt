package com.example.gps_shadow_tracker_app.rest.websocket

import android.location.Location
import com.example.gps_shadow_tracker_app.Constants
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import org.json.JSONObject

class LocationWebSocket {

    private val client: OkHttpClient;
    private val request: Request;
    private val webSocket: WebSocket;

    constructor() {
        this.client = OkHttpClient();
        this.request = Request.Builder().url(Constants.WEBSOCKET_URL).build();
        this.webSocket = this.client.newWebSocket(request, LocationWebSocketListener());
        this.client.dispatcher.executorService.shutdown();
    }

    fun sendLocation(location : Location) {
        var locationObject = JSONObject();
        locationObject.put("latitude", location.latitude);
        locationObject.put("longitude", location.longitude);
        val locationString = locationObject.toString();
        this.webSocket.send(locationString);
    }

}