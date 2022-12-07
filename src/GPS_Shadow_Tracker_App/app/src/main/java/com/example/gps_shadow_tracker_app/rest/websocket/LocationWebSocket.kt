package com.example.gps_shadow_tracker_app.rest.websocket

import android.app.Activity
import android.content.Context
import android.location.Location
import android.util.Log
import com.example.gps_shadow_tracker_app.Constants
import com.example.gps_shadow_tracker_app.ui.UIMapView
import okhttp3.*
import org.json.JSONObject
import java.lang.Exception

class LocationWebSocket : WebSocketListener {

    private val client: OkHttpClient;
    private val request: Request;
    private val webSocket: WebSocket;
    private val mapView: UIMapView;
    private val activity: Activity;

    constructor(context: Context, mapView : UIMapView) : super() {
        this.activity = context as Activity;
        this.client = OkHttpClient();
        this.request = Request.Builder().url(Constants.WEBSOCKET_URL).build();
        this.webSocket = this.client.newWebSocket(request, this);
        this.client.dispatcher.executorService.shutdown();
        this.mapView = mapView;
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
        var location : JSONObject = JSONObject(text);
        if (location.get("type").equals("LOCATION")) {
            activity.runOnUiThread({
                mapView.updatePlayer2Location(location);
            })
        }

        Log.i("PLAYER_2_LOCATION", "Success on this side");

        Log.i("WEBSOCKET_MESSAGE", "TEXT: " + text);
    }

}