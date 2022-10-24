package com.example.gps_shadow_tracker_app.rest

interface RestInterface {

    fun onGetSuccess(response: JSONObject);

    fun onGetFailure();

    fun onPostSuccess(response: JSONObject);

    fun onPostFailure();

}