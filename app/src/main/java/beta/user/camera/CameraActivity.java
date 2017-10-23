package beta.user.camera;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

public class CameraActivity extends AppCompatActivity{
    public static boolean flag_contaGota = false;
    private LayoutImageCamera layoutImage;
    private LayoutScreen layoutScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutScreen = new LayoutScreen(this);
        setContentView(layoutScreen);
        createLayoutImage();

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

    public void click_showFormatos(View v){
        FloatingActionButton fb = (FloatingActionButton)findViewById(R.id.formato_quadrado);
        if(fb.getLabelVisibility() == View.VISIBLE) {
            LayoutScreen.hideButtonFormato();
        }else{
            LayoutScreen.showButtonFormato();
        }
    }
    public void click_contaGota(View v){
        LayoutScreen.hideFab();
        flag_contaGota = true;
        layoutImage.setEventoContaGota();
        Toast.makeText(this,"Clique em cima da cor",Toast.LENGTH_LONG).show();
    }
    public void click_closeApp(View v){
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
                createDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        layoutScreen.inicialize(layoutImage);
    }
    private void createLayoutImage(){
        layoutImage = new LayoutImageCamera(this);
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.layout_fullscreen);
        rl.addView(layoutImage);
        rl.bringChildToFront(findViewById(R.id.fab_menu));
        rl.bringChildToFront(findViewById(R.id.formato_circle));
        rl.bringChildToFront(findViewById(R.id.formato_quadrado));
        rl.bringChildToFront(findViewById(R.id.formato_oval));
        rl.bringChildToFront(findViewById(R.id.corArea_view));
        layoutImage.backColorSeguranca = (GradientDrawable)findViewById(R.id.corArea_view).getBackground();
    }

    public void createDialog(){
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

}
