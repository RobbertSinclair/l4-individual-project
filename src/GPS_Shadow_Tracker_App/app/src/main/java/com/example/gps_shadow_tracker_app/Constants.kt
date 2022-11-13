package com.example.gps_shadow_tracker_app

class Constants {

    companion object {
        val LOCATION_BASE_URL = "https://l4-individual-project-production.up.railway.app"

        val LOCATION_SUBMIT_URL = LOCATION_BASE_URL + "/submit_location";

        val LOCATION_SHADOWS_URL = LOCATION_BASE_URL + "/gps_shadows";

        val LOCATION_SHADOWS_DISTANCE_URL = LOCATION_BASE_URL + "/gps_shadows_nearby/200";

        val DISTANCE_THRESHOLD = 100F;

    }

}
