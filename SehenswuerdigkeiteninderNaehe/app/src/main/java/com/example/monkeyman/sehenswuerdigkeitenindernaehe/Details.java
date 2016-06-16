package com.example.monkeyman.sehenswuerdigkeitenindernaehe;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Details extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        Place p = (Place)intent.getSerializableExtra("place");
        fillDetails(p);
    }

    private void fillDetails(Place p) {
        TextView name = (TextView) findViewById(R.id.name);
        TextView address = (TextView) findViewById(R.id.address);
        TextView types = (TextView) findViewById(R.id.types);
        TextView longitude = (TextView) findViewById(R.id.longitude);
        TextView latitude = (TextView) findViewById(R.id.latitude);

        String typesString = "";
        name.setText(p.name);
        address.setText(p.address);
        for (int i = 0; i < p.types.length; i++) {
            typesString+=", "+p.types[i];
        }
        types.setText(typesString.substring(2));
        longitude.setText(p.longitude+"");
        latitude.setText(p.latitude+"");

    }
}
