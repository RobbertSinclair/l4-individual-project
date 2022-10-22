package com.example.gps_shadow_tracker_app.rest

import android.util.Log
import com.example.gps_shadow_tracker_app.rest.RestInterface

class RestLogger: RestInterface {

    override fun onGetFailure() {
        Log.i("GET REQUEST", "FAILURE");
    }

    override fun onGetSuccess(response: String) {
        Log.i("GET REQUEST", response);
    }

    override fun onPostFailure() {
        Log.i("POST REQUEST", "FAILURE");
    }

    override fun onPostSuccess(response: String) {
        Log.i("POST REQUEST", response);
    }

}