package com.example.gps_shadow_tracker_app

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject



class RestClient {

    private val queue: RequestQueue;

    constructor(context: Context) {
        this.queue = Volley.newRequestQueue(context);
    }

    fun get(url: String, onSuccess: (String) -> Unit, onError: () -> Unit) {
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                onSuccess(response.toString());
            }, {
                onError();
            }
        );

        this.queue.add(request);

    }

    fun post(url: String, body: JSONObject) {
        val request = JsonObjectRequest(Request.Method.POST, url, body,
            { response ->
                Log.i("HTTP Response", "SUCCESS")
            }, {
                Log.i("HTTP Response", "Error");
            }
        );
        this.queue.add(request);

    }


}