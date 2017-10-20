package beta.user.camera;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;

public class CameraActivity extends AppCompatActivity {
    private boolean flag_contaGota;
    private GradientDrawable rlBack;
    private LayoutImageCamera layoutImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag_contaGota = false;
        setContentView(R.layout.activity_camera);
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

        ViewGroup view = (ViewGroup)findViewById(android.R.id.content);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && flag_contaGota) {
                    Rect viewRect = new Rect();
                    layoutImage.getGlobalVisibleRect(viewRect);
                    if (!viewRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        layoutImage.delEventContaGota();
                        flag_contaGota = false;
                    }
                }
                return true;
            }
        });

    }

    public void click_contaGota(View v){
        FloatingActionMenu button = (FloatingActionMenu)findViewById(R.id.fab_menu);
        button.close(true);
        flag_contaGota = true;
        layoutImage.setEventoContaGota();
        Toast.makeText(this,"Clique em cima da cor",Toast.LENGTH_LONG).show();
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

    private void createLayoutImage(){
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.layout_fullscreen);
        layoutImage = new LayoutImageCamera(this);
        rl.addView(layoutImage);
        rl.bringChildToFront(findViewById(R.id.fab_menu));
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
