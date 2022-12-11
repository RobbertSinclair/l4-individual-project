package com.example.gps_shadow_tracker_app.game

import android.location.Location

class Player {

    private var type: PlayerTypes?;
    private var id : String?;
    private var location: Location?;
    private var inShadow: Boolean;

    constructor() {
        this.type = null;
        this.id = null;
        this.location = null;
        this.inShadow = false;

    }

    fun getPlayerType() : PlayerTypes? {
        return this.type;
    }

    fun setPlayerType(type: Boolean) {
        if (type) {
            this.type = PlayerTypes.CHASER;
        } else {
            this.type = PlayerTypes.RUNNER;
        }
    }

    fun setPlayerId(id : String) {
        this.id = id;
    }

    fun setLocation(location: Location) {
        this.location = location;
    }

    fun getPlayerId() : String? {
        return this.id;
    }

}