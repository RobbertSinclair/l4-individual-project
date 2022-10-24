package com.example.gps_shadow_tracker_app.rest

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject



class RestClient {

    private val queue: RequestQueue;
    private val restInterface: RestInterface;

    constructor(context: Context, restInterface: RestInterface) {
        this.queue = Volley.newRequestQueue(context);
        this.restInterface = restInterface;
    }

    fun get(url: String) {
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                restInterface.onGetSuccess(response);
            }, {
                restInterface.onGetFailure();
            }
        );

        this.queue.add(request);

    }

    fun post(url: String, body: JSONObject) {
        val request = JsonObjectRequest(Request.Method.POST, url, body,
            { response ->
                restInterface.onPostSuccess(response);
            }, {
                restInterface.onPostFailure();
            }
        );

        this.queue.add(request);

    }


}