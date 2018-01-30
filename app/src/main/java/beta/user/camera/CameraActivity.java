package beta.user.camera;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;

import beta.user.camera.ShapePack.ShapeDados;

public class CameraActivity extends AppCompatActivity{
    public static boolean flag_contaGota = false;
    private LayoutScreen layoutScreen;
    private SocketClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //client = (SocketClient) getIntent().getSerializableExtra("Socket");
        layoutScreen = new LayoutScreen(this);
        File dir= new File("/storage/emulated/0/Download/imagem_teste.png");
        int[] cor = {68,0,0,255};
        ShapeDados dados = new ShapeDados(BitmapFactory.decodeFile(dir.getAbsolutePath()).copy(Bitmap.Config.ARGB_8888, true),
                cor,200,200,150,150);
        //layoutScreen.inicialize(client.dados);
        layoutScreen.inicialize(dados);
        setContentView(layoutScreen);

       /* View mContentView = findViewById(R.id.layout_fullscreen);
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);*/

        getSupportActionBar().setTitle("Titulo");
    }

    public void click_showBilrho(View v){
        createDialogBrilho();
    }
    public void click_contaGota(View v){
        layoutScreen.hideFab();
        flag_contaGota = true;
        layoutScreen.setEventoContaGota();
        Toast.makeText(this,"Clique em cima da cor",Toast.LENGTH_LONG).show();
    }
    public void click_closeApp(){
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_alt:
                createDialogEditTitle();
                break;
            case R.id.action_sair:
                click_closeApp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        layoutScreen.backColorSeguranca = (GradientDrawable)getResources().getDrawable(R.drawable.button_border);
        //layoutScreen.backColorSeguranca = (GradientDrawable)findViewById(R.id.corArea_view).getBackground();
    }

    public void createDialogEditTitle(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alterar Título");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(getSupportActionBar().getTitle());
        builder.setView(input);

        builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getSupportActionBar().setTitle(input.getText());
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void createDialogBrilho() {
        final SeekBar seek = new SeekBar(this);
        seek.setMax(255);
        seek.setKeyProgressIncrement(1);
        //seek.setProgress(client.brilho);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
            .setIcon(android.R.drawable.btn_star_big_on)
            .setTitle("Iluminação")
            .setView(seek);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //client.brilho = seek.getProgress();
                layoutScreen.hideFab();
                dialog.cancel();
            }
        });

        builder.show();
    }

    /*public void createDialogFormatos(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Formato da Área de Segurança")
                .setSingleChoiceItems(R.array.ArrayFormatos,layoutScreen.idShape, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        layoutScreen.setShape(which, null);
                        layoutScreen.idShape = which;

                    }
                });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                layoutScreen.hideFab();
                dialog.cancel();
            }
        });

        builder.show();
    }*/
}
