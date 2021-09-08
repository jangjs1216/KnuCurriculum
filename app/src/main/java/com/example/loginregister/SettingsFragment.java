package com.example.loginregister;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {
    private SharedPreferences pref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();

        pref.registerOnSharedPreferenceChangeListener(listner);
    }

    @Override
    public void onPause() {
        super.onPause();
        pref.unregisterOnSharedPreferenceChangeListener(listner);

    }

    private SharedPreferences.OnSharedPreferenceChangeListener listner = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.e("settings", key);
            if(key.equals("notification")){
                Log.e("settings", key);
                boolean b= pref.getBoolean("notification",true);
                //여기에 알람 안받도록 구현해야함
            }
            if(key.equals("push")){
                Log.e("settings",key);
                boolean b= pref.getBoolean("notification",true);
            }

        }
    };

}

