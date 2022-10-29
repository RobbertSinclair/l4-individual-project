package com.example.gps_shadow_tracker_app.ui

import android.content.Context
import android.location.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
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
        this.marker = this.map.addMarker(
            MarkerOptions()
                .position(LatLng(0.0, 0.0))
                .title("Current Location")
        );
        this.map.moveCamera(CameraUpdateFactory.zoomTo(15.0F));
        this.gpsShadows.getGpsShadows();
    }

    override fun updateLocation(location: Location) {
        val newCoords = LatLng(location.latitude, location.longitude);
        if (!newCoords.equals(this.coords)) {
            this.marker?.remove();
            this.marker = this.map.addMarker(
                MarkerOptions()
                    .position(newCoords)
                    .title("Current Location")
            );

            this.coords = newCoords;
            if (!changedLocation) {
                this.map.moveCamera(CameraUpdateFactory.newLatLng(this.coords));
                changedLocation = true;
            }
        }

    }



}