package com.example.gps_shadow_tracker_app.ui

import android.content.Context
import android.graphics.Color
import android.location.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class UIMapView : OnMapReadyCallback, UILocationWidget {

    private lateinit var map: GoogleMap;
    private lateinit var gpsShadows : UIGpsShadows;
    private var marker: Marker?;
    private val context : Context;
    private var coords: LatLng;
    private var changedLocation: Boolean;


    constructor(context : Context, mapFragment: SupportMapFragment) {
        this.context = context;
        mapFragment.getMapAsync(this);
        this.coords = LatLng(0.0, 0.0);
        this.marker = null;
        changedLocation = false;

    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap;
        this.gpsShadows = UIGpsShadows(context, map);
        this.coords = LatLng(0.0, 0.0);
        addLocationMarker();
        this.map.moveCamera(CameraUpdateFactory.zoomTo(15.0F));
        this.gpsShadows.getGpsShadows();
    }

    override fun updateLocation(location: Location) {
        val newCoords = LatLng(location.latitude, location.longitude);
        if (!newCoords.equals(this.coords)) {
            this.coords = newCoords;
            this.marker?.remove();
            addLocationMarker();
            if (location.accuracy >= 3.8) {
                addGPSShadowToMap(location);
            }
            if (!changedLocation) {
                this.map.moveCamera(CameraUpdateFactory.newLatLng(this.coords));
                changedLocation = true;
            }
        }
    }

    private fun addGPSShadowToMap(location: Location) {
        this.map.addCircle(
            CircleOptions()
                .center(this.coords)
                .radius(location.accuracy.toDouble())
                .strokeColor(Color.RED)
                .fillColor(Color.RED)
        )
    }

    private fun addLocationMarker() {
        this.marker = this.map.addMarker(
            MarkerOptions()
                .position(this.coords)
                .title("Current Location")
        );

    }



}