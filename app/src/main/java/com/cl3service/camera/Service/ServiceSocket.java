package com.cl3service.camera.Service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.cl3service.camera.CameraActivity;
import com.cl3service.camera.tools.DialogError;
import com.cl3service.camera.R;
import com.cl3service.camera.ShapePack.ShapeDados;
import com.cl3service.camera.tools.DoMethodAfterExecute;

/**
 * Created by Lucas on 25/01/2018.
 */

public class ServiceSocket extends Service {
    private DataInputStream  entrada;
    private DataOutputStream  saida;
    private DialogError dg;
    private ProgressDialog pDialog;
    private ServerSocket server;
    private Activity activity;
    private List<DoMethodAfterExecute> listOfMethods;
    public boolean client_connected = false;

    private final IBinder mBinder = new LocalBinder();
    public Socket client;

    public ShapeDados dados;
    public String[] login = new String[2];

    private BitmapFactory.Options options;
    private BitmapFactory.Options optionsMask;

    @Override
    public void onCreate() {
        try {
            options = new BitmapFactory.Options();
            options.inDensity = DisplayMetrics.DENSITY_MEDIUM;
            options.inTargetDensity = getResources().getDisplayMetrics().densityDpi;
            options.inScaled = true;
            options.inMutable = true;

            optionsMask = new BitmapFactory.Options();
            optionsMask.inDensity = DisplayMetrics.DENSITY_MEDIUM;
            optionsMask.inTargetDensity = getResources().getDisplayMetrics().densityDpi;
            optionsMask.inScaled = true;
            optionsMask.inMutable = true;

            server = new ServerSocket(57000);
            listOfMethods = new ArrayList<DoMethodAfterExecute>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class LocalBinder extends Binder {
        public ServiceSocket getService() {
            return ServiceSocket.this;
        }
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
       /* try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return false;
    }

    public boolean execute(NameTask task){
        return execute(task, null);
    }

    public boolean execute(NameTask task, DoMethodAfterExecute... methods){
        if(client != null && !client_connected){
            new ConnectClient(task).execute();
            return true;
        }

        if(methods != null )
            for(DoMethodAfterExecute method : methods)
                listOfMethods.add(method);

        switch (task){
            case LOGIN:
                new ConnectClient().execute();
                return true;
            case GET_ALL:
                new GetAllDataTask().execute();
                break;
            case GET_IMAGE:
                new GetImageTask().execute();
                break;
            case UPDATE_DADOS:
                new SaveDadosTask().execute();
                break;
        }
        return false;
    }

    private void doMethod(){
        if( !listOfMethods.isEmpty() ){
            DoMethodAfterExecute method = listOfMethods.remove(0);
            method.run();
        }
    }

    private class ConnectClient extends AsyncTask<Void, Void, ServiceSocket.Status> {
        private String error;
        private Button btn_send;
        private NameTask taskToDo;

        ConnectClient(){
            this.btn_send = (Button) activity.findViewById(R.id.login_send);;
        }

        ConnectClient(NameTask task){
            taskToDo = task;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(activity);
            pDialog.setTitle("Aguardando Conexão...");
            pDialog.setMessage("Por favor conecte o cabo USB na placa e aguarde a conexão ser realizada.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected ServiceSocket.Status doInBackground(Void... params) {
            try {
                client = server.accept();
                client.setKeepAlive(true);

                entrada = new DataInputStream(client.getInputStream());
                saida = new DataOutputStream(client.getOutputStream());

                byte[] msg = "login".getBytes();
                saida.writeInt(msg.length);
                saida.write(msg);

                int length = entrada.readInt();
                byte[] ret = new byte[length];
                entrada.readFully(ret, 0, ret.length);

                login = new String(ret, "UTF-8").split(";");
                //login = new String[]{"admin", "admin"};
                client_connected = true;
                return ServiceSocket.Status.SUCCESS;
            }catch (Exception e) {
                error = e.getMessage();
                e.printStackTrace();
                return ServiceSocket.Status.ERROR;
            }
        }
        @Override
        protected void onPostExecute(final ServiceSocket.Status st) {
            pDialog.dismiss();
            switch (st) {
                case SUCCESS:
                    if(taskToDo != null){
                        ServiceSocket.this.execute(taskToDo);
                    }else
                        btn_send.setEnabled(true);
                    break;
                case ERROR:
                    btn_send.setEnabled(false);
                    dg = new DialogError(activity, "Falha ao conectar-se na placa.", error);
                    dg.builder.setPositiveButton("Fechar",new ReconectEvent(this));
                    dg.show();
                    break;
            }
        }
    }

    private class GetAllDataTask extends AsyncTask<Void, Void, ServiceSocket.Status> {
        private String error;

        @Override
        protected ServiceSocket.Status doInBackground(Void... voids) {
            try {
                byte[] msg = "get_all".getBytes();
                saida.writeInt(msg.length);
                saida.write(msg);
                //saida.flush();

                int length = entrada.readInt();
                byte[] ret = new byte[length];
                entrada.readFully(ret, 0, ret.length);

                length = entrada.readInt();
                byte[] img = new byte[length];
                entrada.readFully(img, 0, img.length);

                length = entrada.readInt();
                byte[] img2 = new byte[length];
                entrada.readFully(img2, 0, img2.length);

                length = entrada.readInt();
                byte[] img3 = new byte[length];
                entrada.readFully(img3, 0, img3.length);

                //BitmapFactory.decodeByteArray(img2, 0, img2.length, options)
                //BitmapFactory.decodeStream(new ByteArrayInputStream(img), null, options);
                dados = new ShapeDados(BitmapFactory.decodeStream(new ByteArrayInputStream(img), null, options),
                        BitmapFactory.decodeStream(new ByteArrayInputStream(img2), null, optionsMask),
                        BitmapFactory.decodeStream(new ByteArrayInputStream(img3), null, optionsMask),
                        getResources().getDisplayMetrics().density,
                        new String(ret, "UTF-8")
                );

                /*String str = "limit_width:300\n" +
                        "limit_height:300\n" +
                        "x:0\n" +
                        "y:0\n" +
                        "width:100\n" +
                        "height:100\n" +
                        "cor_rgb:152,55,12\n" +
                        "cor_hsv:152,69,2\n" +
                        "altura:40.0\n" +
                        "gain:-1\n" +
                        "expo:9233.0\n" +
                        "balance_temp:7500\n";
                //BitmapFactory.decodeResource(activity.getResources(), R.drawable.image, options)
                //BitmapFactory.decodeFile("/sdcard/image.bmp", options)
                dados = new ShapeDados(BitmapFactory.decodeResource(activity.getResources(), R.drawable.image, options),
                        BitmapFactory.decodeResource(activity.getResources(), R.drawable.image_mask, optionsMask),
                        getResources().getDisplayMetrics().density,
                        str
                        );
*/
            } catch (Exception e) {
                error = e.getMessage();
                e.printStackTrace();
                return ServiceSocket.Status.ERROR;
            }
            return ServiceSocket.Status.SUCCESS;
        }
        @Override
        protected void onPostExecute(final ServiceSocket.Status st) {
            switch (st) {
                case SUCCESS:
                    Intent intent = new Intent(activity, CameraActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                    break;
                case ERROR:
                    client_connected = false;
                    dg = new DialogError(activity, "Falha ao pegar as configurações da camera.", error );
                    dg.builder.setPositiveButton("Fechar",new ReconectEvent(this));
                    dg.show();
                    break;
            }
        }
    }

    private class GetImageTask extends AsyncTask<Void, Void, ServiceSocket.Status> {
        private String error;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(activity);
            pDialog.setTitle("Atualizando Imagem...");
            pDialog.setMessage("Por favor aguarde a imagem ser atualizada.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected ServiceSocket.Status doInBackground(Void... voids) {
            try {
                byte[] msg =  "get_image".getBytes();
                saida.writeInt(msg.length);
                saida.write(msg);
                //saida.flush();

                int length = entrada.readInt();
                byte[] img = new byte[length];
                entrada.readFully(img, 0, img.length);

                length = entrada.readInt();
                byte[] img2 = new byte[length];
                entrada.readFully(img2, 0, img2.length);

                length = entrada.readInt();
                byte[] img3 = new byte[length];
                entrada.readFully(img3, 0, img3.length);

                options.inBitmap = dados.getImgOri();
                //dados.setImgOri( BitmapFactory.decodeResource(activity.getResources(), R.drawable.image, options) );
                dados.setImgOri( BitmapFactory.decodeStream(new ByteArrayInputStream(img), null, options) );

                //dados.setImgMask( BitmapFactory.decodeResource(activity.getResources(), R.drawable.image_mask, optionsMask) );
                //dados.setImgMask2( BitmapFactory.decodeResource(activity.getResources(), R.drawable.image_mask, optionsMask) );
                dados.setImgMask( BitmapFactory.decodeStream(new ByteArrayInputStream(img2), null, optionsMask) );
                dados.setImgMask2( BitmapFactory.decodeStream(new ByteArrayInputStream(img3), null, optionsMask) );

                ((CameraActivity) activity).layoutScreen.newImage();

            } catch (Exception e) {
                error = e.getMessage();
                e.printStackTrace();
                return ServiceSocket.Status.ERROR;
            }
            return ServiceSocket.Status.SUCCESS;
        }
        @Override
        protected void onPostExecute(final ServiceSocket.Status st) {
            pDialog.dismiss();
            switch (st) {
                case SUCCESS:
                    if(activity instanceof CameraActivity) {
                        dados.updateImg = false;
                        Toast.makeText(activity, "Imagem Atualizada", Toast.LENGTH_SHORT).show();
                        ((CameraActivity) activity).loadImg();
                        doMethod();
                    }
                    break;
                case ERROR:
                    client_connected = false;
                    dg = new DialogError(activity, "Falha ao atualizar a imagem da camera.", error);
                    dg.builder.setPositiveButton("Fechar",new ReconectEvent(this));
                    dg.show();
                    break;
            }
        }
    }

    private class SaveDadosTask extends AsyncTask<Void, Void, ServiceSocket.Status> {
        private String error;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(activity);
            pDialog.setTitle("Salvando Informações..");
            pDialog.setMessage("Por favor aguarde as informações serem salvas e não desconecte o cabo USB.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected ServiceSocket.Status doInBackground(Void... voids) {
            try {
                String sDados = getStringConfig();

                if( dados.check_save_config(sDados) ) {
                    byte[] msg = "set_dados".getBytes();
                    saida.writeInt(msg.length);
                    saida.write(msg);
                    saida.flush();

                    byte[] bDados = sDados.getBytes();
                    saida.writeInt(bDados.length);
                    saida.write(bDados);
                    saida.flush();

                    int length = entrada.readInt();
                    byte[] ret = new byte[length];
                    entrada.readFully(ret, 0, ret.length);

                    if( new String(ret, "UTF-8").compareTo("ok") == 0){
                        dados.setConfig(sDados);
                        return ServiceSocket.Status.SUCCESS;
                    }else
                        return ServiceSocket.Status.INVALID;

                    //dados.setConfig(sDados);
                    //return ServiceSocket.Status.SUCCESS;
                }

                return ServiceSocket.Status.NO_SAVE;

            } catch (Exception e) {
                error = e.getMessage();
                e.printStackTrace();
                return ServiceSocket.Status.ERROR;
            }
           // error = "Não foi possivel salvar os dados";
          //  return ServiceSocket.Status.ERROR;
        }
        @Override
        protected void onPostExecute(final ServiceSocket.Status st) {
            pDialog.dismiss();
            switch (st) {
                case SUCCESS:
                    dados.setCorHsvSave();
                    dados.updateImg = true;
                    dados.saveDados = false;
                    Toast.makeText(activity,"Salvado com sucesso",Toast.LENGTH_SHORT).show();
                    doMethod();
                    /*Toast.makeText(activity,"X:" + dados.x + " |Y:" +dados.y,Toast.LENGTH_LONG).show();
                   ;
                    try {
                        File path = new File("/storage/emulated/0/Download");
                        File file = new File(path, "imag.jpg");
                        FileOutputStream out = new FileOutputStream(file);
                        dados.getImg().compress(Bitmap.CompressFormat.JPEG, 90, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/

                    break;
                case NO_SAVE:
                    Toast.makeText(activity,"Não há alterações à serem salvas.",Toast.LENGTH_SHORT).show();
                    doMethod();
                    break;
                case INVALID:
                    Toast.makeText(activity,"Falha ao salvar as informações, tente novamente.",Toast.LENGTH_SHORT).show();
                    listOfMethods.clear();
                    break;
                case ERROR:
                    client_connected = false;
                    dg = new DialogError(activity, "Erro ao salvar as informações.", error);
                    dg.builder.setPositiveButton("Fechar",new ReconectEvent(this));
                    dg.show();
                    break;
            }
        }
    }

    public class ReconectEvent implements DialogInterface.OnClickListener {
        Object obj;
        ReconectEvent(AsyncTask obj){
            this.obj = obj;
        }
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if(obj instanceof ConnectClient)
                execute(NameTask.LOGIN);
            else if(obj instanceof GetAllDataTask)
                execute(NameTask.GET_ALL );
            else if(obj instanceof GetImageTask)
                execute(NameTask.GET_IMAGE);
            else if(obj instanceof SaveDadosTask)
                execute(NameTask.UPDATE_DADOS );
        }
    }

    public String getStringConfig(){
        int x = Math.round(dados.getOriginalPixel(dados.y));
        int y = Math.round(dados.getOriginalPixel((dados.img_width))) -  Math.round(dados.getOriginalPixel(dados.width)) -  Math.round(dados.getOriginalPixel(dados.x));
        int width = Math.round(dados.getOriginalPixel(dados.height));
        int height = Math.round(dados.getOriginalPixel(dados.width));

        if(x + width > 752) x = x - (x + width - 752);
        if(y + height > 480) y = y - (y + height - 480);

        String sDados = "";
        sDados += "limit_width:" + Integer.toString(  Math.round(dados.getOriginalPixel(dados.limit_height)) ) + "\n";
        sDados += "limit_height:" + Integer.toString(  Math.round(dados.getOriginalPixel(dados.limit_width)) ) + "\n";
        sDados += "x:" + Integer.toString( x ) + "\n";
        sDados += "y:" + Integer.toString( y ) + "\n";
        sDados += "width:" + Integer.toString( width ) + "\n";
        sDados += "height:" + Integer.toString( height ) + "\n";
        sDados += "cor_rgb:" +
                Integer.toString( dados.cor[1] ) + "," +
                Integer.toString( dados.cor[2] ) + "," +
                Integer.toString( dados.cor[3] ) + "\n";
        if(dados.cor_x >= 0 && dados.cor_y >= 0 ) {
            sDados += "cor_hsv:" + Integer.toString(Math.round(dados.getOriginalPixel(dados.cor_y))) + "," + Integer.toString(Math.round(dados.getOriginalPixel(dados.img_width - dados.cor_x)) ) + "\n";
        }else {
            sDados += "cor_hsv:-1,-1\n";
        }
        sDados += "altura:" + Float.toString( dados.get_alturaCam() ) + "\n";
        sDados += "gain:"+ ( dados.gain_auto ? Integer.toString(-1): Float.toString(dados.gain) ) + "\n";
        sDados += "expo:" + ( dados.exposure_auto ? Integer.toString(-1): Float.toString(dados.getExpoMs() * 1000) ) + "\n";
        sDados += "balance_temp:"+ ( dados.balance_auto ? Integer.toString(-1): Integer.toString(dados.balance_temperature_value) )  + "\n";
        return sDados;
    }

    public enum NameTask{
        LOGIN,
        GET_ALL,
        GET_IMAGE,
        UPDATE_DADOS
    }

    public enum Status{
        SUCCESS,
        INVALID,
        NO_SAVE,
        ERROR
    }
}
