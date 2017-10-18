package beta.user.camera;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;

import java.io.File;

public class Main2Activity extends AppCompatActivity {
    private Bitmap bitmapOrig;
    private Paint paint = new Paint();
    private ImageView myImage;
    private View.OnTouchListener contaGonta_click;
    private int[] cor_areaSegura = new int[4];
    private boolean flag_contaGota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag_contaGota = false;
        setContentView(R.layout.activity_main2);

       /* View mContentView = findViewById(R.id.layout_fullscreen);
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);*/

        getSupportActionBar().setTitle("Titulo");
        setEventContaGota();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
        paint.setStrokeWidth(5f);
        myImage = (ImageView) findViewById(R.id.imageView);

        File dir= new File("/storage/emulated/0/Pictures/Screenshots/Screenshot_2017-10-16-11-26-37.png");

        if(dir.exists()){
            bitmapOrig = BitmapFactory.decodeFile(dir.getAbsolutePath());
            drawShape();
        }

        ViewGroup view = (ViewGroup)findViewById(android.R.id.content);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(flag_contaGota) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Rect viewRect = new Rect();
                        myImage.getGlobalVisibleRect(viewRect);
                        if (!viewRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                            delEventContaGota();
                        }
                    }
                }
                return true;
            }
        });

    }

    public void drawShape(){
        Bitmap mBtmp = bitmapOrig.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mBtmp);
        canvas.drawCircle(mBtmp.getWidth()/2, mBtmp.getHeight()/2, 200, paint);

        myImage.setImageBitmap(mBtmp);
    }

    public void click_contaGota(View v){
        FloatingActionMenu button = (FloatingActionMenu)findViewById(R.id.fab_menu);
        button.close(true);
        flag_contaGota = true;
        myImage.setImageBitmap(bitmapOrig);
        myImage.setOnTouchListener(contaGonta_click);
        Toast.makeText(this,"Clique em cima da cor",Toast.LENGTH_LONG).show();
    }
    private void setEventContaGota(){
        contaGonta_click = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int pixel = bitmapOrig.getPixel((int)event.getX(), (int)event.getY());
                cor_areaSegura[0] = Color.alpha(pixel);
                cor_areaSegura[1] = Color.red(pixel);
                cor_areaSegura[2] = Color.green(pixel);
                cor_areaSegura[3] = Color.blue(pixel);
                GradientDrawable background = (GradientDrawable )findViewById(R.id.corArea_view).getBackground();
                background.setColor(pixel);
                delEventContaGota();
                return false;
            }
        };
    }
    public void delEventContaGota(){
        myImage.setOnTouchListener(null);
        flag_contaGota = false;
        drawShape();
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
