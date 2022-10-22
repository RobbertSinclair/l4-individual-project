package com.example.gps_shadow_tracker_app.rest

interface RestInterface {

    fun onGetSuccess(response: String);

    fun onGetFailure();

    fun onPostSuccess(response: String);

    fun onPostFailure();

}