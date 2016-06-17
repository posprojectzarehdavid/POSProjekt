package com.example.monkeyman.sehenswuerdigkeitenindernaehe;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by MonkeyMan on 16.06.2016.
 */
public class Karte extends FragmentActivity implements OnMapReadyCallback{
    GoogleMap map;
    LatLng latLng;
    CameraPosition camera;
    Place p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment);
        Intent intent = getIntent();
        Bundle params = intent.getExtras();
        p = null;
        if (params != null) {
            p = (Place) params.get("Place");
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        latLng = new LatLng(p.getLatitude(), p.getLongitude());
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setMyLocationEnabled(true);
        camera = CameraPosition.builder().target(latLng).zoom(14).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(camera));
        map.addMarker(new MarkerOptions().position(latLng).title(p.getName()));
    }
}