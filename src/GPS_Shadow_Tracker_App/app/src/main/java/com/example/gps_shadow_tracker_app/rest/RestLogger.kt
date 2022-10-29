package com.example.gps_shadow_tracker_app.rest

import android.util.Log
import org.json.JSONObject;


class RestLogger: RestInterface {

    override fun onGetFailure() {
        Log.i("GET REQUEST", "FAILURE");
    }

    override fun onGetSuccess(response: JSONObject) {
        Log.i("GET REQUEST", response.toString());
    }

    override fun onPostFailure() {
        Log.i("POST REQUEST", "FAILURE");
    }

    override fun onPostSuccess(response: JSONObject) {
        Log.i("POST REQUEST", response.toString());
    }

}