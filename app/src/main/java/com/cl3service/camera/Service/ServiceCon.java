package com.cl3service.camera.Service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.cl3service.camera.CameraActivity;
import com.cl3service.camera.LoginActivity;
import com.cl3service.camera.preferences.AbstractPreferenceActivity;
import com.cl3service.camera.preferences.GainExpoPreference;
import com.cl3service.camera.preferences.PropPreference;
import com.cl3service.camera.preferences.WhiteBalancePreference;

/**
 * Created by Lucas on 19/04/2018.
 */

public class ServiceCon implements ServiceConnection {
    public ServiceSocket service;
    public boolean mBound = false;
    private Activity activity;

    public ServiceCon( Activity activity){
        this.activity = activity;
    }

    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
        ServiceSocket.LocalBinder binder = (ServiceSocket.LocalBinder) service;
        this.service = binder.getService();
        this.service.setActivity(activity);
        mBound = true;

        if(activity instanceof LoginActivity)
            this.service.execute(ServiceSocket.NameTask.LOGIN );

        if(activity instanceof CameraActivity)
            ((CameraActivity)this.activity).inicializaImagem();

        if(activity instanceof PropPreference)
            ((PropPreference)this.activity).inicializarDados();
        if(activity instanceof GainExpoPreference)
            ((GainExpoPreference)this.activity).inicializarDados();
        if(activity instanceof WhiteBalancePreference)
            ((WhiteBalancePreference)this.activity).inicializarDados();
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
        mBound = false;
    }
}
