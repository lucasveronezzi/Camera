package beta.user.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;

import beta.user.camera.ShapePack.CircleShape;

public class LayoutImageCamera extends LinearLayout {
    private int[] cor_areaSegura = new int[4];

    private ImageView viewCamera;

    static final int MOVE_NONE = 0;
    static final int MOVE_DRAG = 1;
    static final int MOVE_ZOOM = 2;

    private int move_mode = MOVE_NONE;
    private float oldDist = 1f;
    private float oldScale = 0;
    public GradientDrawable backColorSeguranca;

    private CircleShape shape;

    public LayoutImageCamera(Context context) {
        super(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int x = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, getResources().getDisplayMetrics());
        lp.setMargins(x,0,0,0);
        lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        setLayoutParams(lp);

        setOrientation(VERTICAL);
        setBackgroundResource(R.drawable.border_imageview);
        viewCamera = new ImageView(context);
        viewCamera.setLayoutParams(new ViewGroup.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        viewCamera.setAdjustViewBounds(true);
        addView(viewCamera);
        initialize();
        viewCamera.setOnTouchListener(touchEvent_Move);
    }

    public LayoutImageCamera(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public LayoutImageCamera(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void updateImage(){
        shape.drawShape();
        viewCamera.setImageBitmap(shape.getBtmpShow());
    }

    private void initialize(){
        File dir= new File("/storage/emulated/0/Pictures/Screenshots/Screenshot_2017-10-16-11-26-37.png");
        if(dir.exists()){
            shape = new CircleShape(BitmapFactory.decodeFile(dir.getAbsolutePath()).copy(Bitmap.Config.ARGB_8888, true));
            shape.drawShape();
            viewCamera.setImageBitmap(shape.getBtmpShow());
        }
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
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
                    LayoutScreen.hideFab();
                    move_mode = MOVE_DRAG;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        move_mode = MOVE_ZOOM;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(move_mode == MOVE_DRAG){
                        shape.x = (int)event.getX();
                        if(shape.x < 0)
                            shape.x = 0 ;
                        else if(shape.x +  shape.width > shape.getBtmpOrig().getWidth())
                            shape.x = shape.getBtmpOrig().getWidth() - shape.width;
                        shape.y = (int)event.getY();
                        if(shape.y < 0)
                            shape.y = 0;
                        else if(shape.y + shape.height >  shape.getBtmpOrig().getHeight())
                            shape.y = shape.getBtmpOrig().getHeight() - shape.height;

                        Log.i("GetW",Float.toString(viewCamera.getWidth()));
                        Log.i("GetH",Float.toString(viewCamera.getHeight()));
                        Log.i("GetX",Float.toString(shape.x));
                        Log.i("GetY",Float.toString(shape.y));
                        updateImage();
                    }else if(move_mode == MOVE_ZOOM && event.getPointerCount() == 2){
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


                                shape.x -= tot/2;
                                if(shape.x < 0) shape.x = 0;
                                else if(shape.x +  shape.width > shape.getBtmpOrig().getWidth())
                                    shape.x = shape.getBtmpOrig().getWidth() - shape.width;;

                                shape.y -= tot/2;
                                if(shape.y < 0) shape.y = 0;
                                else if(shape.y +  shape.height > shape.getBtmpOrig().getHeight())
                                    shape.y = shape.getBtmpOrig().getHeight() - shape.height;;
                                shape.resize();
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
