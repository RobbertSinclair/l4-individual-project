package com.example.gps_shadow_tracker_app.ui

import android.app.Activity
import android.content.Context
import android.location.Location
import android.widget.TextView
import com.example.gps_shadow_tracker_app.R

class UILocationTextViews : UILocationWidget {
    private val latLabel: TextView?;
    private val longLabel: TextView?;
    private val accuracyLabel: TextView?;

    constructor(context: Context) {
        val activity = context as Activity;
        latLabel = null;
        longLabel = null;
        accuracyLabel = null;
        initValues();
    }

    fun initValues() {
        latLabel?.text = "0";
        longLabel?.text = "0";
        accuracyLabel?.text = "0";
    }

    override fun updateLocation(location: Location) {
        latLabel?.text = location.latitude.toString();
        longLabel?.text = location.longitude.toString();
        accuracyLabel?.text = location.accuracy.toString();
    }

    fun setLatitudeLabel(latitude: String) {
        latLabel?.text = latitude;
    }

    fun setLongitudeLabel(longitude: String) {
        longLabel?.text = longitude;
    }

    fun setAccuracyLabel(accuracy: String) {
        accuracyLabel?.text = accuracy;
    }

}