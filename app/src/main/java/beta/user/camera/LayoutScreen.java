package beta.user.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;

import beta.user.camera.ShapePack.CircleShape;
import beta.user.camera.ShapePack.OvalShape;
import beta.user.camera.ShapePack.RecShape;
import beta.user.camera.ShapePack.Shapes;

public class LayoutScreen extends RelativeLayout {
    private FloatingActionMenu fbMenu;
    private Rect fabRect;

    private int[] cor_areaSegura = new int[4];

    private ImageView viewCamera;

    static final int MOVE_NONE = 0;
    static final int MOVE_DRAG = 1;
    static final int MOVE_RESIZE = 2;

    public int idShape = 0;
    private int move_mode = MOVE_NONE;
    private float oldDist = 1f;
    private float oldX = 0;
    private float oldY = 0;
    private float oldScale = 0;
    public GradientDrawable backColorSeguranca;

    private File dir;

    private Shapes shape;

    public LayoutScreen(Context context) {
        super(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(lp);

        LayoutInflater inflater = LayoutInflater.from(context);
        View inflatedLayout= inflater.inflate(R.layout.activity_camera, null, false);
        addView(inflatedLayout);

        fbMenu = (FloatingActionMenu)findViewById(R.id.fab_menu);
        fabRect = new Rect();
        viewCamera = new ImageView(context);
        inicialize();
    }

    public void inicialize(){
        viewCamera.setLayoutParams(
                new ViewGroup.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        viewCamera.setAdjustViewBounds(true);
        ((LinearLayout)findViewById(R.id.layoutImage)).addView(viewCamera);
        viewCamera.setOnTouchListener(touchEvent_Move);
        fbMenu.bringToFront();
        //dir= new File("/storage/emulated/0/Pictures/Screenshots/Screenshot_2017-10-16-11-26");
        dir= new File("/storage/emulated/0/Download/imagem_teste.png");

        if(dir.exists()){
            setShape(0);
        }

    }

    public void hideFab(){
        if(fbMenu.isShown()){
            fbMenu.close(true);
        }
    }

    private void updateImage(){
        shape.drawShape();
        viewCamera.setImageBitmap(shape.getBtmpShow());
    }

    public void setShape(int id){
        switch (id){
            case 0:
                shape = new CircleShape(BitmapFactory.decodeFile(dir.getAbsolutePath()).copy(Bitmap.Config.ARGB_8888, true));
                shape.drawShape();
                ((FloatingActionButton)findViewById(R.id.formato)).setImageResource(R.mipmap.ic_circle);
                viewCamera.setImageBitmap(shape.getBtmpShow());
                break;
            case 1:
                shape = new RecShape(BitmapFactory.decodeFile(dir.getAbsolutePath()).copy(Bitmap.Config.ARGB_8888, true));
                shape.drawShape();
                ((FloatingActionButton)findViewById(R.id.formato)).setImageResource(R.mipmap.ic_rect);
                viewCamera.setImageBitmap(shape.getBtmpShow());
                break;
            case 2:
                shape = new OvalShape(BitmapFactory.decodeFile(dir.getAbsolutePath()).copy(Bitmap.Config.ARGB_8888, true));
                shape.drawShape();
                ((FloatingActionButton)findViewById(R.id.formato)).setImageResource(R.mipmap.ic_elipse);
                viewCamera.setImageBitmap(shape.getBtmpShow());
                break;
        }
    }

    public void setEventoContaGota(){
        viewCamera.setImageBitmap(shape.getBtmpOrig());
        viewCamera.setOnTouchListener(null);
        viewCamera.setOnTouchListener(touchEvent_contaGota);
    }
    public void delEventContaGota(){
        viewCamera.setOnTouchListener(null);
        viewCamera.setOnTouchListener(touchEvent_Move);
        updateImage();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                    if (CameraActivity.flag_contaGota) {
                        Rect viewRect = new Rect();
                        viewCamera.getGlobalVisibleRect(viewRect);
                        if (!viewRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                            delEventContaGota();
                            CameraActivity.flag_contaGota = false;
                        }
                    }
                fbMenu.getGlobalVisibleRect(fabRect);
                if (fabRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {

                }
                break;

        }
        return false;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    private OnTouchListener touchEvent_contaGota = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int pixel = shape.getBtmpOrig().getPixel((int) event.getX(), (int) event.getY());
            cor_areaSegura[0] = Color.alpha(pixel);
            cor_areaSegura[1] = Color.red(pixel);
            cor_areaSegura[2] = Color.green(pixel);
            cor_areaSegura[3] = Color.blue(pixel);
            backColorSeguranca.setColor(pixel);
            delEventContaGota();
            return false;
        }
    };

    private OnTouchListener touchEvent_Move = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.i("X",Float.toString(event.getX()));
            Log.i("Y",Float.toString(event.getY()));
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    hideFab();
                    oldX = event.getX();
                    oldY = event.getY();
                    move_mode = MOVE_DRAG;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        move_mode = MOVE_RESIZE;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(move_mode == MOVE_DRAG){
                        shape.x = shape.x + event.getX() - oldX;
                        shape.y = shape.y + event.getY() - oldY;

                        Log.i("GetW",Float.toString(viewCamera.getWidth()));
                        Log.i("GetH",Float.toString(viewCamera.getHeight()));
                        Log.i("GetX",Float.toString(shape.x));
                        Log.i("GetY",Float.toString(shape.y));
                        oldX = event.getX();
                        oldY = event.getY();
                        updateImage();
                    }else if(move_mode == MOVE_RESIZE && event.getPointerCount() == 2){
                        float newDist = spacing(event);
                        if (newDist > 10f) {
                            float scale = newDist - oldDist;
                            float tot = scale - oldScale;
                            if(tot > 10 || tot < -10){
                                oldScale = scale;
                                shape.width += tot;
                                shape.height += tot;
                                if(shape.width > shape.getBtmpOrig().getWidth()){
                                    shape.width = shape.getBtmpOrig().getWidth();
                                    break;
                                }else if(shape.width < 10f) {
                                    shape.width = 10f;
                                    break;
                                }
                                if( shape.height > shape.getBtmpOrig().getHeight() ) {
                                    shape.height = shape.getBtmpOrig().getHeight();
                                    break;
                                }else if(shape.height < 10f) {
                                    shape.height = 10f;
                                    break;
                                }
                                shape.resize();

                                shape.x -= tot/2;
                                shape.y -= tot/2;
                                updateImage();
                            }
                            Log.i("scale",Float.toString(scale));
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP: //first finger lifted
                case MotionEvent.ACTION_POINTER_UP: //second finger lifted
                    move_mode = MOVE_NONE;
                    break;
            }
            return true;
        }

    };
}
