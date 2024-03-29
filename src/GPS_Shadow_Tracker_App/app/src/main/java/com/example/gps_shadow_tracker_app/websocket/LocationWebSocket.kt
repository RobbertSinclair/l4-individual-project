package com.example.gps_shadow_tracker_app.websocket

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.gps_shadow_tracker_app.Constants.Companion.GAME_TIME
import com.example.gps_shadow_tracker_app.Constants.Companion.JAIL_DURATION
import com.example.gps_shadow_tracker_app.Constants.Companion.SECOND
import com.example.gps_shadow_tracker_app.game.Player
import com.example.gps_shadow_tracker_app.ui.UIMapView
import com.example.gps_shadow_tracker_app.ui.bigText
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject

class LocationWebSocket : WebSocketListener {

    private var client: OkHttpClient
    private var request: Request
    private var webSocket: WebSocket
    private val mapView: UIMapView
    private val activity: Activity
    private val player: Player
    private var notificationShow: MutableState<Boolean>
    private var textState: MutableState<String>
    private var jailTime: MutableState<Boolean>
    private val scope : CoroutineScope
    private var jailCounter: MutableState<Int>
    private var gameStarted: MutableState<Boolean>
    private var gameTime: MutableState<Int>

    constructor(context: Context, mapView : UIMapView, player: Player) : super() {
        this.activity = context as Activity
        this.client = OkHttpClient()
        this.request = Request.Builder().url(Constants.WEBSOCKET_URL).build()
        this.webSocket = this.client.newWebSocket(request, this)
        this.client.dispatcher.executorService.shutdown()
        this.mapView = mapView
        this.player = player
        this.notificationShow = mutableStateOf(false)
        this.textState = mutableStateOf("")
        this.jailTime = mutableStateOf(false)
        this.scope = CoroutineScope(Dispatchers.Main)
        this.jailCounter = mutableStateOf(60)
        this.gameStarted = mutableStateOf(false)
        this.gameTime = mutableStateOf(GAME_TIME)

    }

    fun setJailTime(value: Boolean) {
        this.jailTime.value = value
    }

    fun sendLocation(locationObject : JSONObject) {
        if (gameStarted.value) {
            val accuracy : Float = locationObject.get("accuracy") as Float;
            player.setMinAccuracy(accuracy);
            locationObject.put("minAccuracy", player.getMinAccuracy());
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

    private fun reconnectWebSocket() {
        this.client = OkHttpClient()
        this.request = Request.Builder().url("${Constants.WEBSOCKET_URL}?ID=${player.getPlayerId()}").build();
        this.webSocket = this.client.newWebSocket(this.request, this);
        this.client.dispatcher.executorService.shutdown();
    }


    fun startGame() {
        gameStarted.value = true;
        timerService()
    }

    fun endGame() {
        gameStarted.value = false;
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason);
        Log.i("WEBSOCKET_CLOSED", "Reason " + reason);
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response);
        val connectObject = JSONObject();
        connectObject.put("type", "CONNECT");
        connectObject.put("brand", Build.BRAND);
        connectObject.put("product", Build.PRODUCT);
        connectObject.put("model", Build.MODEL)
        this.webSocket.send(connectObject.toString());
        Log.i("WEBSOCKET_CREATED", "Response: $response");

    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason);
        Log.i("WEBSOCKET_CLOSING", "Reason $reason");
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response);
        Log.i("WEBSOCKET_FAILED", "Response $t");
        reconnectWebSocket();
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text);

        Log.i("WEBSOCKET_MESSAGE", "TEXT: $text");
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
        jailCounter.value = JAIL_DURATION;
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

    fun setTime(newTime: Int) {
        gameTime.value = newTime;
    }

    fun timerService() {
        if (gameTime.value <= 0) {
            gameTime.value = GAME_TIME;
        }
        this.scope.launch {
            while (gameTime.value > 0 && gameStarted.value) {
                delay(SECOND);
                gameTime.value--;
            }
            val endGameObject = JSONObject();
            endGameObject.put("type", "END_GAME");
            webSocket.send(endGameObject.toString());
            endGame()
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
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
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

    @Composable
    fun timer() {
        var time = remember { this.gameTime};
        var minutes = (time.value / 60).toInt();
        var seconds = time.value % 60;
        var timeText = String.format("%02d:%02d", minutes, seconds);
        bigText("Time ${timeText}")

    }


}
