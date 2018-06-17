package com.cl3service.camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatDrawableManager;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.cl3service.camera.Service.ServiceCon;
import com.cl3service.camera.Service.ServiceSocket;

import java.io.File;

public class LoginActivity extends AppCompatActivity{
    public TextView usuario;
    public TextView senha;
    private ServiceCon con;
    public View include_form;
    public View include_loading;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuario = (TextView) findViewById(R.id.usuario);
        senha = (TextView) findViewById(R.id.senha);

        include_form = findViewById(R.id.include_form);
        include_loading = findViewById(R.id.include_loading);
        include_form.setVisibility(View.VISIBLE);
        include_loading.setVisibility(View.GONE);

        con = new ServiceCon(this);
        bindService(new Intent(this, ServiceSocket.class), con, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(con);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    public void click_login(View v){
        if(validate()){
            include_form.setVisibility(View.GONE);
            include_loading.setVisibility(View.VISIBLE);
            con.service.execute( ServiceSocket.NameTask.GET_ALL);
        }
    }

    public boolean validate(){
        if(usuario.getText().length() < 3 || usuario.getText().toString().compareTo(con.service.login[0]) != 0){
            Toast.makeText(this,"Usuário inválido", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(senha.getText().length() < 4 || senha.getText().toString().compareTo(con.service.login[1]) != 0 ){
            Toast.makeText(this,"Senha inválida", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

