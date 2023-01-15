package com.example.gps_shadow_tracker_app.websocket

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gps_shadow_tracker_app.Constants
import com.example.gps_shadow_tracker_app.Constants.Companion.SECOND
import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.ui.UIMapView
import com.example.gps_shadow_tracker_app.ui.bigText
import kotlinx.coroutines.*
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
    private var jailTime: MutableState<Boolean>;
    private val scope : CoroutineScope;
    private var jailCounter: MutableState<Int>;
    private var gameStarted: MutableState<Boolean>;

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
        this.jailTime = mutableStateOf(false);
        this.scope = CoroutineScope(Dispatchers.Main);
        this.jailCounter = mutableStateOf(60);
        this.gameStarted = mutableStateOf(false);
    }

    fun setJailTime(value: Boolean) {
        this.jailTime.value = value;
    }

    fun sendLocation(locationObject : JSONObject) {
        if (!jailTime.value && gameStarted.value) {
            val accuracy : Float = locationObject.get("accuracy") as Float;
            locationObject.put("type", "LOCATION");
            locationObject.put("inShadow", accuracy >= Constants.SHADOW_THRESHOLD);
            locationObject.put("player", player.getPlayerId())
            val locationString = locationObject.toString();
            Log.i("LOCATION_STRING", locationString);
            this.webSocket.send(locationString);
        }
    }

    fun startGameButtonClick() {
        startGame();
        val gameStartJson = JSONObject();
        gameStartJson.put("type", "START_GAME");
        this.webSocket.send(gameStartJson.toString())
    }

    fun startGame() {
        gameStarted.value = true;
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

    fun jailTimeService() {
        jailCounter.value = 60;
        this.scope.launch {
            jailTimeDelay();
        }

    }

    suspend fun jailTimeDelay() = coroutineScope {
        launch {
            Log.i("COUROUTINE", this.coroutineContext.toString());
            setJailTime(true);
            Log.i("JAIL TIME", "Jail Time Turned On");
            while (jailCounter.value > 0) {
                delay(SECOND);
                jailCounter.value--;
            }
            setJailTime(false);
            Log.i("JAIL TIME", "Jail Time Turned Off")
        }
    }

    fun notificationService(locationObject: JSONObject) {
        this.scope.launch {
            displayTextNotification(locationObject);
        }

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

    @Composable
    fun jailTimeLabel() {
        var showing = remember { this.jailTime };
        var time = remember { this.jailCounter }

        if (showing.value) {
            bigText("JAIL TIME: ${time.value}")
        }

    }

    @Composable
    fun gameLobbySurface() {
        val showing = remember { this.gameStarted }
        if (!showing.value) {
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight().fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            Log.i("BUTTON CLICK", "START GAME")
                            startGameButtonClick()
                        }
                    ) {
                        Text(fontSize = 22.sp, fontWeight = FontWeight.Bold, text = "Start Game")
                    }
                }
            }
        }
    }
}
