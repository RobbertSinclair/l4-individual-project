package com.example.gps_shadow_tracker_app.ui

import android.location.Location
import android.util.Log
import androidx.compose.runtime.*
import java.util.*


class UILocationTextViews : UILocationWidget {
    private var accuracyValue: String;
    private var accuracyState: MutableState<String>

    constructor() {
        accuracyValue = "0.0";
        this.accuracyState = mutableStateOf(accuracyValue)
    }

    override fun updateLocation(location: Location) {
        accuracyValue = String.format("%.1f", location.accuracy);
        accuracyState.value = accuracyValue;
        Log.i("LOCATION UPDATED", accuracyValue)
    }

    fun getAccuracy() : String {
        Log.i("GET ACCURACY CALLED", "Value $this.accuracyValue");
        return this.accuracyValue;
    }

    @Composable
    fun accuracyLabel() {
        val accuracy = this.accuracyState;
        bigText("Error: ${accuracy.value} meters");
    }


}

