package com.example.gps_shadow_tracker_app

class Constants {

    companion object {
        val LOCATION_BASE_URL = "https://gpsshadows.pythonanywhere.com"

        val LOCATION_SUBMIT_URL = LOCATION_BASE_URL + "/submit_location";

        val LOCATION_SHADOWS_URL = LOCATION_BASE_URL + "/gps_shadows";

    }

}
