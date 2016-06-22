package com.example.monkeyman.sehenswuerdigkeitenindernaehe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
        new HttpGetBitmap().execute(p.getIcon());
    }

    class HttpGetBitmap extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            String link = params[0];
            HttpURLConnection connection = null;
            Bitmap bitmap = null;
            try {
                URL url = new URL(link);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setAllowUserInteraction(false);
                connection.setInstanceFollowRedirects(true);
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                Log.i("abc", e.getLocalizedMessage());
            } finally {
                if (null != connection) {
                    connection.disconnect();
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            latLng = new LatLng(p.getLatitude(), p.getLongitude());
            MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map_fragment);
            map = mapFragment.getMap();
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            map.setMyLocationEnabled(true);
            camera = CameraPosition.builder().target(latLng).zoom(14).build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(camera));
            map.addMarker(new MarkerOptions().position(latLng).title(p.getName()).
                    icon(BitmapDescriptorFactory.fromBitmap(bitmap)).anchor(0.5f, 1f));
            super.onPostExecute(bitmap);
        }
    }
}
