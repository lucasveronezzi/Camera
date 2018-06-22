package com.cl3service.camera.tools;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;

/**
 * Created by Lucas on 23/01/2018.
 */
public class DialogError {
    private AlertDialog dialog;
    private boolean showing = false;
    public Builder builder;
    public DialogError(@NonNull final Activity activity, final String titulo, String erro) {
        builder = new Builder(activity);
        builder.setTitle(titulo);
        builder.setMessage(erro);
        builder.setCancelable(false);
    }
    public void show(){
        dialog = builder.create();
        showing = true;
        dialog.show();
    }

    public boolean isShowing(){
        return showing;
    }
}