package com.example.gps_shadow_tracker_app.ui

import android.content.Context
import android.graphics.Color
import android.location.Location
import android.util.Log
import com.example.gps_shadow_tracker_app.Constants
import com.example.gps_shadow_tracker_app.rest.RestClient
import com.example.gps_shadow_tracker_app.rest.RestInterface
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONObject

class UIGpsShadows: RestInterface {

    private val map : GoogleMap;
    private val restClient : RestClient;
    private var location: Location;
    private var counter: Int;

    constructor(context : Context, map : GoogleMap, location: Location) {
        this.map = map;
        this.restClient = RestClient(context, this);
        this.location = location;
        this.counter = 0;
    }

    fun getGpsShadows() {
        var locationObject = JSONObject();
        locationObject.put("latitude", this.location.latitude);
        locationObject.put("longitude", this.location.longitude);
        Log.i("GPS_SHADOWS", locationObject.toString());
        this.restClient.post(Constants.LOCATION_SHADOWS_DISTANCE_URL, locationObject);
    }

    fun checkLocationFurtherThanDistance(other: Location) {
        Log.i("DISTANCE", counter.toString());
        if (Constants.IS_RUNNER && counter % Constants.DISTANCE_THRESHOLD == 0F) {
            location = other;
            this.getGpsShadows();
        }
        this.counter++;
    }

    override fun onPostSuccess(response: JSONObject) {
        val locationDicts : JSONArray = response.get("locations") as JSONArray;
        for (i in 0 until locationDicts.length()) {
            val location = locationDicts.getJSONObject(i);
            val coords = LatLng(location.getDouble("latitude"), location.getDouble("longitude"));
            val accuracy = location.getDouble("accuracy");
            this.map.addCircle(CircleOptions()
                .center(coords)
                .radius(Constants.SHADOW_CIRCLE_RADIUS)
                .strokeColor(Color.RED)
                .fillColor(Color.RED)
            );
        }
    }

    override fun onGetFailure() {
        Log.i("ERROR", "The Get Request Failed");
    }

    override fun onPostFailure() {
        Log.i("ERROR", "The Post Request Failed");
    }

    override fun onGetSuccess(response: JSONObject) {
        TODO("Not yet implemented")
    }

}