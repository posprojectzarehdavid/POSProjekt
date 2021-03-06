package com.example.monkeyman.sehenswuerdigkeitenindernaehe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class Details extends Activity {
    Place p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        Bundle params = intent.getExtras();
        p = null;
        if(params != null){
            p = (Place)params.get("Place");
        }
        fillDetails(p);
    }

    private void fillDetails(Place p) {
        TextView name = (TextView) findViewById(R.id.name);
        TextView address = (TextView) findViewById(R.id.address);
        TextView types = (TextView) findViewById(R.id.types);
        TextView longitude = (TextView) findViewById(R.id.longitude);
        TextView latitude = (TextView) findViewById(R.id.latitude);

        String typesString = "";
        name.setText(p.getName());
        address.setText(p.getAddress());
        for (int i = 0; i < p.getTypes().length; i++) {
            typesString+=", "+p.getTypes()[i];
        }
        types.setText(typesString.substring(2));
        longitude.setText(p.getLongitude() + "");
        latitude.setText(p.getLatitude() + "");
    }

    public void showMap(View view){
        Intent intent = new Intent(getApplicationContext(), Karte.class);
        intent.putExtra("Place", p);
        startActivity(intent);
    }
}
