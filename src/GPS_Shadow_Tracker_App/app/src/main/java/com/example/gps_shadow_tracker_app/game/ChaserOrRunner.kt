package com.example.gps_shadow_tracker_app.game

import androidx.compose.runtime.Composable
import com.example.gps_shadow_tracker_app.ui.bigText

@Composable
fun chaserOrRunner(player: Player) {
    if (player.getPlayerType() == PlayerTypes.CHASER) {
        bigText("Chaser")
    } else {
        bigText("Runner")
    }
}