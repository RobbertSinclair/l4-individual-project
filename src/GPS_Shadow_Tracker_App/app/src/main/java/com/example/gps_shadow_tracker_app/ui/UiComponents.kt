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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun bigText(text: String) {
    Card() {
        Text(text, modifier= Modifier.padding(horizontal=4.dp, vertical=4.dp), fontSize=25.sp)
    }
}

@Composable
fun accuracyAndPlayerMode(accuracy: Double, player: Player) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        accuracyLabel(accuracy)
        chaserOrRunner(player)
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun accuracyLabel(accuracy: Double) {
    bigText("Error: $accuracy meters");
}

@Composable
fun mapView() {
    val singapore = LatLng(1.35, 103.87)
    val singaporeState = MarkerState(position = singapore)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = singaporeState,
            title = "Marker in Singapore"
        )
    }
}

@Preview
@Composable
fun previewAccuracyAndPlayerMode() {
    MaterialTheme {
        accuracyAndPlayerMode(5.0, true);
    }
}