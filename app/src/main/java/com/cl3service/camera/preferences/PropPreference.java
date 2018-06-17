package com.cl3service.camera.preferences;

import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.cl3service.camera.CameraActivity;
import com.cl3service.camera.R;
import com.cl3service.camera.Service.ServiceSocket;
import com.cl3service.camera.tools.DoMethodAfterExecute;

import java.lang.reflect.Method;

public class PropPreference extends AbstractPreferenceActivity {
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
            setHasOptionsMenu(true);
        }
    }

    public void inicializarDados(){
        editorPref.putString(key_altura, Float.toString( con.service.dados.get_alturaCam() ) );
        editorPref.commit();
        PreferenceManager.setDefaultValues(this, R.xml.pref_main, true);

        fragmentPref.addPreferencesFromResource(R.xml.pref_main);
        bindPreferenceSummaryToValue(fragmentPref.findPreference(key_altura));

        firstChange = false;
    }
}
