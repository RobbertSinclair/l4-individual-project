package com.example.gps_shadow_tracker_app.rest.websocket

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class LocationWebSocketListener : WebSocketListener()  {

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