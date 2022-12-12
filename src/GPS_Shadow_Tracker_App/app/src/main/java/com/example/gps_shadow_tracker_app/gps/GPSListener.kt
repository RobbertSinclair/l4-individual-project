package com.example.gps_shadow_tracker_app.gps

import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.util.Log
import com.example.gps_shadow_tracker_app.Constants
import com.example.gps_shadow_tracker_app.rest.RestClient
import com.example.gps_shadow_tracker_app.rest.RestLogger
import com.example.gps_shadow_tracker_app.rest.websocket.LocationWebSocket
import com.example.gps_shadow_tracker_app.ui.UILocationWidget
import org.json.JSONObject

class GPSListener: LocationListener {

    private val activity: Activity;
    private val locationWidgets: List<UILocationWidget>;
    private val webSocket : LocationWebSocket;

    constructor(context: Context, locationWidgets: List<UILocationWidget>, webSocket: LocationWebSocket) {
        this.activity = context as Activity;
        this.locationWidgets = locationWidgets
        this.webSocket = webSocket;
    }

    fun createLocationObject(location: Location) : JSONObject {
        var locationObject = JSONObject();
        locationObject.put("latitude", location.latitude);
        locationObject.put("longitude", location.longitude);
        locationObject.put("accuracy", location.accuracy);
        return locationObject;
    }

    override fun onLocationChanged(location: Location) {
        Log.i("Location Changed", location.toString());
        Log.i("Location Accuracy", location.accuracy.toString());
        for (widget in locationWidgets) {
            widget.updateLocation(location);
        }
        val locationJSON: JSONObject = createLocationObject(location);
        webSocket.sendLocation(locationJSON);
    }
}