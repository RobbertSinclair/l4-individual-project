package com.example.gps_shadow_tracker_app.ui

import android.content.Context
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import com.example.gps_shadow_tracker_app.Constants
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
    private var playerMarker: Marker?;
    private val context : Context;
    private var coords: LatLng;
    private var changedLocation: Boolean;


    constructor(context : Context, mapFragment: SupportMapFragment) {
        this.context = context;
        mapFragment.getMapAsync(this);
        this.coords = LatLng(0.0, 0.0);
        this.playerMarker = null;
        changedLocation = false;

    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap;
        this.gpsShadows = UIGpsShadows(context, map, Location(LocationManager.GPS_PROVIDER));
        this.coords = LatLng(0.0, 0.0);
        addLocationMarker();
        this.map.moveCamera(CameraUpdateFactory.zoomTo(15.0F));
        this.gpsShadows.getGpsShadows();
    }

    override fun updateLocation(location: Location) {
        val newCoords = LatLng(location.latitude, location.longitude);
        if (!newCoords.equals(this.coords)) {
            this.gpsShadows.checkLocationFurtherThanDistance(location);
            this.coords = newCoords;
            this.playerMarker?.remove();
            addLocationMarker();
            if (location.accuracy >= Constants.SHADOW_THRESHOLD) {
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
                .radius(Constants.SHADOW_CIRCLE_RADIUS)
                .strokeColor(Color.RED)
                .fillColor(Color.RED)
        )
    }

    private fun addLocationMarker() {
        this.playerMarker = this.map.addMarker(
            MarkerOptions()
                .position(this.coords)
                .title("Current Location")
        );

    }



}