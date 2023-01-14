package com.example.gps_shadow_tracker_app.websocket

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gps_shadow_tracker_app.Constants
import com.example.gps_shadow_tracker_app.Constants.Companion.MINUTE
import com.example.gps_shadow_tracker_app.Constants.Companion.SECOND
import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.ui.UIMapView
import com.example.gps_shadow_tracker_app.ui.bigText
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.*
import org.json.JSONObject

class LocationWebSocket : WebSocketListener {

    private val client: OkHttpClient;
    private val request: Request;
    private val webSocket: WebSocket;
    private val mapView: UIMapView;
    private val activity: Activity;
    private val player: Player;
    private var notificationShow: MutableState<Boolean>;
    private var textState: MutableState<String>;
    private var transmitLocation: Boolean;

    constructor(context: Context, mapView : UIMapView, player: Player) : super() {
        this.activity = context as Activity;
        this.client = OkHttpClient();
        this.request = Request.Builder().url(Constants.WEBSOCKET_URL).build();
        this.webSocket = this.client.newWebSocket(request, this);
        this.client.dispatcher.executorService.shutdown();
        this.mapView = mapView;
        this.player = player;
        this.notificationShow = mutableStateOf(false);
        this.textState = mutableStateOf("");
        this.transmitLocation = true;
    }

    fun toggleLocationTransmit() {
        this.transmitLocation = !this.transmitLocation;
    }

    fun sendLocation(locationObject : JSONObject) {
        val accuracy : Float = locationObject.get("accuracy") as Float;
        locationObject.put("type", "LOCATION");
        locationObject.put("inShadow", accuracy >= Constants.SHADOW_THRESHOLD);
        locationObject.put("player", player.getPlayerId())
        val locationString = locationObject.toString();
        Log.i("LOCATION_STRING", locationString);
        this.webSocket.send(locationString);
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason);
        Log.i("WEBSOCKET_CLOSED", "Reason " + reason);
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response);
        val connectObject = JSONObject();
        connectObject.put("type", "CONNECT");
        this.webSocket.send(connectObject.toString());
        Log.i("WEBSOCKET_CREATED", "Response: " + response.toString());

    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason);
        Log.i("WEBSOCKET_CLOSING", "Reason " + reason);
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response);
        Log.i("WEBSOCKET_FAILED", "Response " + t.toString());
        throw t;
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text);

        Log.i("WEBSOCKET_MESSAGE", "TEXT: " + text);
        try {
            var message = JSONObject(text);
            notificationService(message);
            WebSocketActions.valueOf(message.getString("type")).implementAction(this, player, message, mapView);
        } catch (e: Exception) {
            Log.i("INVALID MESSAGE", "There isn't a valid type of action here")
        }
        Log.i("PLAYER_2_LOCATION", "Success on this side");
    }

    fun locationTransmitService() = runBlocking {
        jailTimeDelay();
    }

    suspend fun jailTimeDelay() = coroutineScope {
        launch {
            toggleLocationTransmit();
            delay(MINUTE);
            toggleLocationTransmit();
        }
    }

    fun notificationService(locationObject: JSONObject) = runBlocking {
        displayTextNotification(locationObject);
    }

    suspend fun displayTextNotification(locationObject: JSONObject) = coroutineScope {
        launch {
            switchOnNotification(locationObject)
            delay(SECOND);
            switchOffNotification();
        }
    }

    private fun switchOnNotification(locationObject: JSONObject) {
        if (locationObject.has("message")) {
            this.textState.value = locationObject.getString("message");
            this.notificationShow.value = true;
        }
    }

    private fun switchOffNotification() {
        this.notificationShow.value = false;
    }

    @Composable
    fun notificationCenter() {
        var showing = remember { this.notificationShow };
        var text = remember { this.textState }
        if (showing.value) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
                horizontalAlignment= Alignment.CenterHorizontally) {
                bigText(text = text.value);
            }
        }

    }

}
