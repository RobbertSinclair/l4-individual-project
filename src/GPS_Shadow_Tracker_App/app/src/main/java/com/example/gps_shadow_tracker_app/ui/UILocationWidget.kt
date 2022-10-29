package com.example.gps_shadow_tracker_app.ui

import android.location.Location

interface UILocationWidget {

    fun updateLocation(location: Location);

}