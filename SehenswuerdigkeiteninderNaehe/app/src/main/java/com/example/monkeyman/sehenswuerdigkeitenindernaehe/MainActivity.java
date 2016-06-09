package com.example.monkeyman.sehenswuerdigkeitenindernaehe;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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

public class MainActivity extends AppCompatActivity {
    ListView lv;
    ArrayList place_data;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        new HttpGetTask().execute();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,place_data);

    }

    private void initialize() {
        place_data = new ArrayList();
        lv = (ListView)findViewById(R.id.listView);
    }

    class HttpGetTask extends AsyncTask<Void, Void, ArrayList<Place>> {

        private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        private static final String MY_URL = "https://api.themoviedb.org/3/list/5316bbcd92514158d20015b5?api_key=4a93cc5ebfaa6555f836661d5f57ad60";
        private static final String URL = "https://maps.googleapis.com/maps/api/place/details/json?location=-33.8670522,151.1957362&radius=500&key=AIzaSyAJrFjbRJUQ-pS0rPmm13hYNnbxcxcTsNg";
        private static final String URL_NEARBY = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&key=AIzaSyDGGz7Sj364SNYOI5eHQXFv9w5TG5-Jez0";

        @Override
        protected ArrayList<Place> doInBackground(Void... params) {
            String data = "";
            ArrayList<Place> places = null;
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
                        JSONArray typ = object.getJSONArray("types");
                        String []types = new String[typ.length()];
                        for(int j = 0; j<typ.length(); j++){
                            types[j]=typ.getString(j);
                        }
                        String vicinity = object.getString("vicinity");
                        Place p = new Place(latitude,longitude,name,icon,vicinity,types);
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
            place_data.addAll(places);
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
