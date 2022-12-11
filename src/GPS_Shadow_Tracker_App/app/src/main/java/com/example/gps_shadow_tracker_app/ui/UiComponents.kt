package com.example.gps_shadow_tracker_app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.game.chaserOrRunner

@Composable
fun bigText(text: String) {
    Card() {
        Text(text, modifier= Modifier.padding(horizontal=4.dp, vertical=4.dp), fontSize=25.sp)
    }
}

@Composable
fun accuracyAndPlayerMode(textView: UILocationTextViews, player: Player) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        textView.accuracyLabel()
        chaserOrRunner(player)
    }
    Spacer(modifier = Modifier.height(8.dp))
}


@Preview
@Composable
fun previewAccuracyAndPlayerMode() {
    MaterialTheme {
        accuracyAndPlayerMode(UILocationTextViews(), Player());
    }
}