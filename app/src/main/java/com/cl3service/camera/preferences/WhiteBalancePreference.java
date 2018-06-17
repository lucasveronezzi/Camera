package com.cl3service.camera.preferences;

import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.cl3service.camera.R;

public class WhiteBalancePreference extends AbstractPreferenceActivity {
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
        editorPref.putBoolean(key_balance_auto, con.service.dados.balance_auto);
        editorPref.putString(key_balance_temperature_value, Integer.toString(con.service.dados.balance_temperature_value) );
        editorPref.commit();
        PreferenceManager.setDefaultValues(this, R.xml.pref_main, true);

        fragmentPref.addPreferencesFromResource(R.xml.pref_white_balance);
        bindPreferenceSummaryToBool(fragmentPref.findPreference(key_balance_auto));
        bindPreferenceSummaryToValue(fragmentPref.findPreference(key_balance_temperature_value));

        firstChange = false;
   }

}
