package com.cl3service.camera.preferences;

import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.cl3service.camera.R;

public class GainExpoPreference extends AbstractPreferenceActivity  {
    public static float expo_max;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentPref = new MainPreferenceFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, fragmentPref).commit();
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }

    public void inicializarDados(){
        expo_max = con.service.dados.getExpoMaxMS();

        editorPref.putBoolean(key_gain_auto, con.service.dados.gain_auto );
        editorPref.putFloat(key_gain_value, con.service.dados.gain );
        editorPref.putBoolean(key_expo_auto, con.service.dados.exposure_auto );
        editorPref.putFloat(key_expo_value, con.service.dados.getExpoMs() );
        editorPref.commit();
        PreferenceManager.setDefaultValues(this, R.xml.pref_main, true);

        fragmentPref.addPreferencesFromResource(R.xml.pref_gain_expo);
        bindPreferenceSummaryToBool(fragmentPref.findPreference(key_gain_auto));
        bindPreferenceSummaryToFloat(fragmentPref.findPreference(key_gain_value));
        bindPreferenceSummaryToBool(fragmentPref.findPreference(key_expo_auto));
        bindPreferenceSummaryToFloat(fragmentPref.findPreference(key_expo_value));

        firstChange = false;
    }

}
