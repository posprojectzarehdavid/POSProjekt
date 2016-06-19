package com.example.monkeyman.sehenswuerdigkeitenindernaehe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by MonkeyMan on 16.06.2016.
 */
public class Karte extends Activity{
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
        latLng = new LatLng(p.getLatitude(), p.getLongitude());
        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map_fragment);
        map = mapFragment.getMap();
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.setMyLocationEnabled(true);
        camera = CameraPosition.builder().target(latLng).zoom(14).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(camera));
        map.addMarker(new MarkerOptions().position(latLng).title(p.getName()));
    }
}
