package com.example.monkeyman.sehenswuerdigkeitenindernaehe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements LocationListener {
    ListView lv;
    ArrayList<Place> place_data;
    ArrayAdapter adapter;
    LocationManager manager;
    Parameter parameter;
    Location location;
    Geocoder gcd;
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
        location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        if (location == null) {
            onResume();
            Toast.makeText(this, "Location nicht gefunden", Toast.LENGTH_SHORT).show();
        } else {
            parameter.setLatitude(location.getLatitude());
            parameter.setLongitude(location.getLongitude());
            parameter.setAltitude(location.getAltitude());
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
        if (id == R.id.action_update){
            onResume();
            return true;
        }
        if (id == R.id.action_info){
            showLocationData();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void showLocationData() {
        List<Address> addresses;
        String [] info = new String[4];
        try {
            addresses = gcd.getFromLocation(parameter.getLatitude(), parameter.getLongitude(), 1);
            if (addresses != null && addresses.size() >= 0) {
                if(addresses.get(0).getAddressLine(0)!=null) {
                    info[0] = addresses.get(0).getAddressLine(0);
                } else info[0] = "Straße nicht bekannt";
                if(addresses.get(0).getLocality()!=null) {
                    info[1] = addresses.get(0).getLocality();
                } else info[1] = "Ortschaft nicht bekannt";
                if(addresses.get(0).getCountryName()!=null) {
                    info[2] = addresses.get(0).getCountryName();
                } else info[2] = "Land nicht bekannt";
                if(addresses.get(0).getPostalCode()!=null) {
                    info[3] = addresses.get(0).getPostalCode();
                } else info[3] = "Postleitzahl nicht bekannt";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("Wo befinde ich mich?");
        View v = getLayoutInflater().inflate(R.layout.info_layout,null);
        ab.setView(v);
        TextView strasse = (TextView) v.findViewById(R.id.textViewStr);
        TextView plz = (TextView) v.findViewById(R.id.textViewPLZ);
        TextView ort = (TextView) v.findViewById(R.id.textViewOrt);
        TextView land = (TextView) v.findViewById(R.id.textViewLand);
        TextView latitude = (TextView) v.findViewById(R.id.textViewLat);
        TextView longitude = (TextView) v.findViewById(R.id.textViewLong);
        TextView altitude = (TextView) v.findViewById(R.id.textViewAlt);

        strasse.setText(info[0]);
        plz.setText(info[3]);
        ort.setText(info[1]);
        land.setText(info[2]);
        latitude.setText(parameter.getLatitude()+"");
        longitude.setText(parameter.getLongitude()+"");
        altitude.setText(parameter.getAltitude()+"");
        ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        ab.create().show();
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
        private String URL_NEARBY;

        @Override
        protected ArrayList<Place> doInBackground(Parameter... params) {
            Parameter parameter = params[0];
            URL_NEARBY = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                    parameter.getLatitude() + "," + parameter.getLongitude() + "&radius=" + parameter.getRadius() +
                    "&key=AIzaSyDGGz7Sj364SNYOI5eHQXFv9w5TG5-Jez0";
            Log.i("meineurl", URL_NEARBY);
            String data = "";
            ArrayList<Place> places = new ArrayList<>();
            HttpURLConnection httpURLConnection = null;
            int counter = 0;
            try {
                httpURLConnection = (HttpURLConnection) new URL(URL_NEARBY).openConnection();
                httpURLConnection.setAllowUserInteraction(false);
                httpURLConnection.setInstanceFollowRedirects(true);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                data = readStream(in);
                JSONObject json = null;

                try {
                    json = new JSONObject(data);
                    JSONArray place_data = json.optJSONArray("results");
                    for (int i = 0; i < place_data.length(); i++) {
                        JSONObject object = place_data.optJSONObject(i);
                        JSONObject geometry = object.optJSONObject("geometry");
                        JSONObject location = geometry.optJSONObject("location");
                        double latitude = location.optDouble("lat");
                        double longitude = location.optDouble("lng");
                        String icon = object.optString("icon");
                        String name = object.optString("name");
                        JSONArray typ = object.optJSONArray("types");
                        String[] types = new String[typ.length()];
                        for (int j = 0; j < typ.length(); j++) {
                            types[j] = typ.optString(j);
                        }
                        String vicinity = object.optString("vicinity");
                        Place p = new Place(latitude, longitude, name, icon, vicinity, types);
                        Log.i("place", p.toString());
                        places.add(p);
                    }
                } catch (JSONException e) {
                    Log.i("abc", e.getLocalizedMessage());
                }
            } catch (MalformedURLException e) {
                Log.i("abc", e.getLocalizedMessage());
            } catch (IOException e) {
                Log.i("abc", e.getLocalizedMessage());
            } catch (Exception e) {
                Log.i("abc", e.getLocalizedMessage());
            } finally {
                if (null != httpURLConnection) {
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
            Collections.sort(place_data);
            adapter.notifyDataSetChanged();
            super.onPostExecute(places);
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer data = new StringBuffer("");
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    data.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
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
