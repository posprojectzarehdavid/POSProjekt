package com.example.monkeyman.sehenswuerdigkeitenindernaehe;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by MonkeyMan on 18.06.2016.
 */
/*public class Maps extends Fragment {
    MapView mapView;
    GoogleMap map;
    LatLng latLng;
    CameraPosition camera;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_fragment,container);
        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        map = mapView.getMap();
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.setMyLocationEnabled(true);
        mapView.setVisibility(View.GONE);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void showPlace(Place place){
        mapView.setVisibility(View.VISIBLE);
        latLng = new LatLng(place.getLatitude(), place.getLongitude());
        camera = CameraPosition.builder().target(latLng).zoom(14).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(camera));
        map.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
    }
}*/
