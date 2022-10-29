package com.example.gps_shadow_tracker_app.rest

import org.json.JSONObject;

interface RestInterface {

    fun onGetSuccess(response: JSONObject);

    fun onGetFailure();

    fun onPostSuccess(response: JSONObject);

    fun onPostFailure();

}