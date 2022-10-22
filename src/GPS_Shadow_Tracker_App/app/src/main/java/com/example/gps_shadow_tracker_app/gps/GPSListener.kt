package com.example.gps_shadow_tracker_app.gps

import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.util.Log
import android.widget.TextView
import com.example.gps_shadow_tracker_app.Constants
import com.example.gps_shadow_tracker_app.R
import com.example.gps_shadow_tracker_app.rest.RestClient
import com.example.gps_shadow_tracker_app.rest.RestLogger
import com.example.gps_shadow_tracker_app.ui.LocationTextViews
import org.json.JSONObject

class GPSListener: LocationListener {

    private val activity: Activity;
    private val restClient: RestClient;
    private val locationText: LocationTextViews;

    constructor(context: Context, locationText: LocationTextViews) {
        activity = context as Activity;
        this.locationText = locationText
        restClient = RestClient(context, RestLogger());
    }

    fun createLocationObject(location: Location) : JSONObject {
        var locationMap: HashMap<String, String> = HashMap<String, String>();
        locationMap["latitude"] = location.latitude.toString();
        locationMap["longitude"] = location.longitude.toString();
        locationMap["accuracy"] = location.accuracy.toString();
        return JSONObject(locationMap as Map<*, *>);
    }

    override fun onLocationChanged(location: Location) {
        Log.i("Location Changed", location.toString());
        Log.i("Location Accuracy", location.accuracy.toString());
        locationText.updateLocationLabels(location);
        val locationJSON: JSONObject = createLocationObject(location);
        restClient.post(Constants.LOCATION_SUBMIT_URL, locationJSON);
    }

}