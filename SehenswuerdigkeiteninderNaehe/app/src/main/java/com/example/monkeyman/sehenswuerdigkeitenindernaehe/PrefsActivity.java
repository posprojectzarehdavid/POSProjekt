package com.example.monkeyman.sehenswuerdigkeitenindernaehe;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by darminger on 09.06.2016.
 */
public class PrefsActivity extends PreferenceActivity {

    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }
    public static class MyPreferenceFragment extends PreferenceFragment{
        public void onCreate(final Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);
        }
    }
}
