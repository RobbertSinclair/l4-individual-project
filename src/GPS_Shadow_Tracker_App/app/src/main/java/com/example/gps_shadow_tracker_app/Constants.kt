package com.example.gps_shadow_tracker_app

class Constants {

    companion object {
        val LOCATION_BASE_URL = "https://l4-individual-project-production.up.railway.app"

        val LOCATION_SUBMIT_URL = LOCATION_BASE_URL + "/submit_location";

        val LOCATION_SHADOWS_URL = LOCATION_BASE_URL + "/gps_shadows";

        val LOCATION_SHADOWS_DISTANCE_URL = LOCATION_BASE_URL + "/gps_shadows_nearby/200";

        const val DISTANCE_THRESHOLD = 50F

        const val WEBSOCKET_URL = "wss://l4-individual-project-production.up.railway.app"

        const val SHADOW_CIRCLE_RADIUS: Double = 20.0;

        const val SHADOW_THRESHOLD: Double = 30.0;

        const val LOCATION_DELAY: Long = 2000;

        val IS_RUNNER : Boolean = false;

        const val MIN_DISTANCE: Float = 0F;

        const val SECOND: Long = 1000;

        const val MINUTE : Long = 60 * SECOND;

    }

}
