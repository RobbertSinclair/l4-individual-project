package com.example.gps_shadow_tracker_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.util.*

class MainActivity : AppCompatActivity() {
    private var timer: Timer = Timer();
    private lateinit var latValueLabel : TextView;
    private lateinit var longValueLabel : TextView;
    private var counter = 0;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        latValueLabel = findViewById(R.id.latValueLabel);
        longValueLabel = findViewById(R.id.longValueLabel);
        latValueLabel.text = counter.toString();
        longValueLabel.text = counter.toString();

        timer.scheduleAtFixedRate(object: TimerTask() {
            override fun run() {
                runOnUiThread() {
                    incrementLabels();
                }
            } },0, 1000);


    }

    fun incrementLabels() {
        counter++;
        this.latValueLabel.text = counter.toString();
        this.longValueLabel.text = counter.toString();

    }
}