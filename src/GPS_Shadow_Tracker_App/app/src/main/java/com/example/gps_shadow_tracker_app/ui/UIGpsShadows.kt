package com.example.gps_shadow_tracker_app.ui

import android.content.Context
import android.graphics.Color
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

    constructor(context : Context, map : GoogleMap) {
        this.map = map;
        this.restClient = RestClient(context, this);
    }

    fun getGpsShadows() {
        this.restClient.get(Constants.LOCATION_SHADOWS_URL);
    }

    override fun onGetSuccess(response: JSONObject) {
        val locationDicts : JSONArray = response.get("locations") as JSONArray;
        for (i in 0 until locationDicts.length()) {
            val location = locationDicts.getJSONObject(i);
            val coords = LatLng(location.getDouble("latitude"), location.getDouble("longitude"));
            val accuracy = location.getDouble("accuracy");
            this.map.addCircle(CircleOptions()
                .center(coords)
                .radius(accuracy)
                .strokeColor(Color.RED)
                .fillColor(Color.RED)
            );
        }
    }

    override fun onGetFailure() {
        Log.i("ERROR", "The Get Request Failed")
    }

    override fun onPostFailure() {
        TODO("Not yet implemented")
    }



    override fun onPostSuccess(response: JSONObject) {
        TODO("Not yet implemented")
    }





}