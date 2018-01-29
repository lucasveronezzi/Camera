package beta.user.camera;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import beta.user.camera.ShapePack.ShapeDados;

/**
 * Created by Lucas on 25/01/2018.
 */

public class SocketClient extends Socket {
    private SocketAddress sockaddr;
    private DialogError dg;
    private ProgressDialog pDialog;
    public ShapeDados dados;

    public SocketClient() {
        super();
    }

    public void execute(AppCompatActivity activity, NameTask task){
        switch (task){
            case LOGIN:
                new UserLoginTask((LoginActivity)activity).execute();
            case SEND_DADOS:
                new SendDadosTask((CameraActivity) activity).execute();
        }
    }

    private SocketClient getSocket(){
        return this;
    }

    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private LoginActivity activity;

        UserLoginTask(LoginActivity loginActivity) {
            this.activity = loginActivity;
        }
        @Override
        protected void onPreExecute() {
            activity.include_form.setVisibility(View.GONE);
            activity.include_loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                sockaddr = new InetSocketAddress(InetAddress.getByAddress(new byte[]{127, 0, 0, 1}), 57000);
                getSocket().connect(sockaddr);
                if (getSocket() != null) {
                    PrintWriter out = new PrintWriter(getSocket().getOutputStream(), true);
                    out.write("op=login\nuser="+ activity.usuario.getText().toString()+"\nsenha="+ activity.senha.getText().toString());
                    out.flush();

                    BufferedReader in = new BufferedReader(new InputStreamReader(getSocket().getInputStream() ));
                    if (in.ready()) {
                        String result = in.readLine();
                        if(result == "ok")
                            return true;
                    }
                }else{

                }
            } catch (IOException e) {
                dg = new DialogError(activity, "Falha ao seu conectar na placa.", e.getMessage());
                dg.show();
                e.printStackTrace();
            }
            /*if(s_usuario.equals("admin") && s_senha.equals("admin")){
                return true;
            }*/
            return false;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            if(success){
                Intent intent = new Intent(activity, CameraActivity.class);
                intent.putExtra("Socket", (Serializable) getSocket());
                activity.startActivity(intent);
                activity.finish();
            }else{
                activity.include_form.setVisibility(View.VISIBLE);
                activity.include_loading.setVisibility(View.GONE);
                Toast.makeText(activity,"Login Inválido",Toast.LENGTH_LONG).show();
            }
        }
    }

    private class SendDadosTask extends AsyncTask<Void, Void, Boolean> {
        private CameraActivity activity;

        public SendDadosTask(CameraActivity activity){
            this.activity = activity;
        }
        protected void onPreExecute() {
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Carregando");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            return null;
        }
    }

    public enum NameTask{
        LOGIN,
        SEND_DADOS,
        UPDATE_DADOS
    }
}
