package com.example.monkeyman.sehenswuerdigkeitenindernaehe;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity implements LocationListener {
    ListView lv;
    ArrayList<Place> place_data;
    ArrayAdapter adapter;
    LocationManager manager;
    Parameter parameter;
    Location location;
    SharedPreferences prefs;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    private static final int PERMISSIONS_REQUEST_GPS_ACCESS = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_GPS_ACCESS);

        } else {*/
        initialize();
        Log.i("ich bin hier", "");
        new HttpGetTask().execute(parameter);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, place_data);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Place p = place_data.get(position);
                Intent intent = new Intent(getApplicationContext(), Details.class);
                intent.putExtra("Place", p);
                startActivity(intent);
            }
        });
        //}
    }


    private void initialize() {
        parameter = new Parameter();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                String val = sharedPreferences.getString(key, "");
                String msg = key + " wurde auf " + val + " gesetzt!";
                parameter.setRadius(Integer.parseInt(prefs.getString("Radius", "500")));
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(listener);
        parameter.setRadius(Integer.parseInt(prefs.getString("Radius", "500")));
        place_data = new ArrayList();
        lv = (ListView) findViewById(R.id.listView);

        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, parameter.radius, this);
        location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        parameter.setLatitude(location.getLatitude());
        parameter.setLongitude(location.getLongitude());
        if (location == null) {
            onResume();
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, PrefsActivity.class));
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, parameter.getRadius(), this);
        new HttpGetTask().execute(parameter);
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_GPS_ACCESS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new HttpGetTask().execute(latitude,longitude);
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) return;
        parameter.setLatitude(location.getLatitude());
        parameter.setLongitude(location.getLongitude());
        new HttpGetTask().execute(parameter);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    class HttpGetTask extends AsyncTask<Parameter, Void, ArrayList<Place>> {

        private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        private static final String MY_URL = "https://api.themoviedb.org/3/list/5316bbcd92514158d20015b5?api_key=4a93cc5ebfaa6555f836661d5f57ad60";
        private static final String URL = "https://maps.googleapis.com/maps/api/place/details/json?location=-33.8670522,151.1957362&radius=500&key=AIzaSyAJrFjbRJUQ-pS0rPmm13hYNnbxcxcTsNg";
        private String URL_NEARBY;

        @Override
        protected ArrayList<Place> doInBackground(Parameter... params) {
            Parameter parameter = params[0];
            URL_NEARBY = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+
                    parameter.getLatitude()+","+parameter.getLongitude()+"&radius="+parameter.getRadius()+
                    "&key=AIzaSyDGGz7Sj364SNYOI5eHQXFv9w5TG5-Jez0";
            Log.i("Hallo",URL_NEARBY);
            String data = "";
            ArrayList<Place> places = new ArrayList<>();
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection)new URL(URL_NEARBY).openConnection();
                InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                data = readStream(in);
                JSONObject json = null;

                try {
                    json = new JSONObject(data);
                    JSONArray place_data = json.getJSONArray("results");

                    for(int i = 0 ; i<place_data.length(); i++){
                        JSONObject object = place_data.getJSONObject(i);
                        JSONObject geometry = object.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        double latitude = location.getDouble("lat");
                        double longitude = location.getDouble("lng");
                        String icon = object.getString("icon");
                        String name = object.getString("name");
                        JSONArray photosArray = object.optJSONArray("photos");
                        String[] pictureLinks = null;
                        if(photosArray != null) {
                            pictureLinks = new String[photosArray.length()];
                            for (int j = 0; j < photosArray.length(); j++) {
                                JSONObject photosObject = photosArray.getJSONObject(j);
                                JSONArray html_attributes = photosObject.getJSONArray("html_attributions");
                                pictureLinks[j] = html_attributes.getString(0);
                            }
                        }
                        JSONArray typ = object.getJSONArray("types");
                        String []types = new String[typ.length()];
                        for(int j = 0; j<typ.length(); j++){
                            types[j]=typ.getString(j);
                        }
                        String vicinity = object.getString("vicinity");
                        Place p = new Place(latitude,longitude,name,icon,pictureLinks,vicinity,types);
                        places.add(p);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(null!=httpURLConnection){
                    httpURLConnection.disconnect();
                }
            }
            return places;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> places) {
            progressDialog.dismiss();
            place_data.clear();
            place_data.addAll(places);
            Log.i("hey",place_data.size()+"");
            for(int i = 0; i<place_data.size(); i++){
                Log.i("hey",place_data.get(i).toString());
            }
            adapter.notifyDataSetChanged();
            super.onPostExecute(places);
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer data = new StringBuffer("");
            try{
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while((line = reader.readLine())!=null){
                    data.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(reader!=null){
                    try{
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return data.toString();
        }

        protected void onPreExecute() {
            progressDialog.setMessage("Suche Daten...");
            progressDialog.show();
        }
    }
}
