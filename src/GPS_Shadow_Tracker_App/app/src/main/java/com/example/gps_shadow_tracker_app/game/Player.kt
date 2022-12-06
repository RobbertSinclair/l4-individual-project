package com.example.gps_shadow_tracker_app.game

class Player {

    private var type: PlayerTypes
    private var id : Int?;

    constructor(type: PlayerTypes) {
        this.type = type;
        this.id = null;
    }

    fun getPlayerType() : PlayerTypes {
        return this.type;
    }

    fun setPlayerType(type : PlayerTypes) {
        this.type = type;
    }

    fun setPlayerId(id : Int) {
        this.id = id;
    }



}