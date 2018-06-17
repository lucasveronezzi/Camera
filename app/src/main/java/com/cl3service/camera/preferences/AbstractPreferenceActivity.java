package com.cl3service.camera.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.cl3service.camera.CameraActivity;
import com.cl3service.camera.R;
import com.cl3service.camera.Service.ServiceCon;
import com.cl3service.camera.Service.ServiceSocket;
import com.cl3service.camera.tools.DoMethodAfterExecute;

import java.lang.reflect.Method;

public abstract class AbstractPreferenceActivity extends AppCompatActivity{
    protected ServiceCon con;
    protected SharedPreferences settings;
    protected SharedPreferences.Editor editorPref;
    protected PreferenceFragment fragmentPref;

    protected String key_altura;
    protected String key_gain_auto;
    protected String key_gain_value;
    protected String key_expo_auto;
    protected String key_expo_value;
    protected String key_balance_auto;
    protected String key_balance_temperature_value;

    protected boolean firstChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings =  PreferenceManager.getDefaultSharedPreferences(this);
        editorPref = settings.edit();

        con = new ServiceCon(this);
        bindService(new Intent(this, ServiceSocket.class), con, Context.BIND_AUTO_CREATE);

        key_altura = getResources().getString(R.string.pref_key_altura);
        key_gain_auto = getResources().getString(R.string.pref_key_gain_auto);
        key_gain_value = getResources().getString(R.string.pref_key_gain_value);
        key_expo_auto = getResources().getString(R.string.pref_key_expo_auto);
        key_expo_value = getResources().getString(R.string.pref_key_expo_value);
        key_balance_auto = getResources().getString(R.string.pref_key_balance_auto);
        key_balance_temperature_value = getResources().getString(R.string.pref_key_balance_value);

        firstChange = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (con.service != null) con.service.setActivity(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(con);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_preferences_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                con.service.execute( ServiceSocket.NameTask.UPDATE_DADOS );
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    public void bindPreferenceSummaryToFloat(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getFloat(preference.getKey(), 0));
    }


    public void bindPreferenceSummaryToBool(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), true));
    }

    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            con.service.dados.saveDados = firstChange ? con.service.dados.saveDados : true;
            con.service.dados.updateImg = firstChange ? con.service.dados.updateImg : true;

            String stringValue = value.toString();
            String key = preference.getKey();
            Float floatValue;
            ListPreference listPreference;

            switch (key){
                case "key_altura":
                    floatValue = Float.parseFloat(stringValue);
                    if(floatValue > 50){
                        floatValue = 50f;
                        Toast.makeText(AbstractPreferenceActivity.this,"Altura máxima é de: 80 cm",Toast.LENGTH_LONG).show();
                    }else if(floatValue < 30){
                        floatValue = 30f;
                        Toast.makeText(AbstractPreferenceActivity.this,"Altura mínima é de: 10 cm",Toast.LENGTH_LONG).show();
                    }
                    con.service.dados.set_alturaCam( floatValue );
                    preference.setSummary("Distância de " + floatValue + " cm da mesa.");
                    break;
                case "gain_auto":
                    con.service.dados.gain_auto = (boolean)value;
                    if((boolean)value){
                        fragmentPref.getPreferenceScreen().findPreference(key_gain_value).setEnabled(false);
                        preference.setSummary( getResources().getString(R.string.pref_descricao_auto_on) );
                    }else{
                        fragmentPref.getPreferenceScreen().findPreference(key_gain_value).setEnabled(true);
                        preference.setSummary( getResources().getString(R.string.pref_descricao_auto_off) );
                    }
                    break;
                case "gain_value":
                    floatValue = (float) value;
                    con.service.dados.gain = floatValue;
                    preference.setSummary(floatValue + " decibel");
                    break;
                case "expo_auto":
                    con.service.dados.exposure_auto = (boolean)value;
                    if((boolean)value){
                        fragmentPref.getPreferenceScreen().findPreference(key_expo_value).setEnabled(false);
                        preference.setSummary( getResources().getString(R.string.pref_descricao_auto_on) );
                    }else{
                        fragmentPref.getPreferenceScreen().findPreference(key_expo_value).setEnabled(true);
                        preference.setSummary( getResources().getString(R.string.pref_descricao_auto_off) );
                    }
                    break;
                case "expo_value":
                    floatValue = (float) value;
                    con.service.dados.setExpoMs(floatValue);
                    preference.setSummary(String.format("%.2f",floatValue) + " milissegundos.");
                    break;
                case "balance_auto":
                    con.service.dados.balance_auto = (boolean)value;
                    if((boolean)value){
                        fragmentPref.getPreferenceScreen().findPreference(key_balance_temperature_value).setEnabled(false);
                        preference.setSummary( getResources().getString(R.string.pref_descricao_balance_auto_on) );
                    }else{
                        fragmentPref.getPreferenceScreen().findPreference(key_balance_temperature_value).setEnabled(true);
                        preference.setSummary( getResources().getString(R.string.pref_descricao_balance_auto_off) );
                    }
                    break;
                case "balance_value":
                    listPreference = (ListPreference) preference;
                    con.service.dados.balance_temperature_value = Integer.parseInt(stringValue);
                    preference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue(stringValue)]);
                    break;
            }
            return true;
        }
    };

}
