package com.example.gps_shadow_tracker_app.game

class Player {

    private var type: PlayerTypes?;
    private var id : String?;

    constructor() {
        this.type = null;
        this.id = null;
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

    fun getPlayerId() : String? {
        return this.id;
    }

}