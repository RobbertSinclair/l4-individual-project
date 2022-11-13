package com.example.gps_shadow_tracker_app.rest.websocket

import com.example.gps_shadow_tracker_app.Constants
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

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

}