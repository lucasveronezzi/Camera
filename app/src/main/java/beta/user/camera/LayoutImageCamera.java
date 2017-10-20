package beta.user.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class LayoutImageCamera extends LinearLayout {
    private int[] cor_areaSegura = new int[4];
    private Bitmap btmpOrig;
    private Bitmap btmpShadown;
    private Bitmap btmpAreaSegura;
    private Bitmap btmpShow;
    private Canvas canvasShow;
    private Paint paintInsideStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Rect rect;
    private ImageView viewCamera;
    private int area_x = 200;
    private int area_y = 200;
    private int area_center;
    private int area_witdh = 150;
    private int area_height = 150;


    static final int MOVE_NONE = 0;
    static final int MOVE_DRAG = 1;
    static final int MOVE_ZOOM = 2;

    private int move_mode = MOVE_NONE;
    private float oldDist = 1f;
    private float oldScale = 0;
    public GradientDrawable backColorSeguranca;

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

    private void initialize(){
        paintInsideStroke.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
        paintInsideStroke.setStrokeWidth(5f);
        paintInsideStroke.setStyle(Paint.Style.STROKE);
        paintInsideStroke.setColor(Color.RED);
        paintInsideStroke.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));


        File dir= new File("/storage/emulated/0/Pictures/Screenshots/Screenshot_2017-10-16-11-26-37.png");
        if(dir.exists()){
            btmpOrig =  BitmapFactory.decodeFile(dir.getAbsolutePath()).copy(Bitmap.Config.ARGB_8888, true);
            rect = new Rect(0, 0, area_witdh,area_height);
            setBitMaps();
            btmpAreaSegura = Bitmap.createBitmap(area_witdh, area_height, Bitmap.Config.ARGB_8888);
            drawShape();
        }
    }
    public void setBitMaps(){
        btmpShadown = btmpOrig.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(btmpShadown);
        RectF outerRectangle = new RectF(0, 0, btmpShadown.getWidth(), btmpShadown.getHeight());
        Paint paintOuside = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintOuside.setColor(Color.BLACK);
        paintOuside.setAlpha(64);
        canvas.drawRect(outerRectangle, paintOuside);
        btmpShow = btmpShadown.copy(Bitmap.Config.ARGB_8888, true);
        canvasShow = new Canvas(btmpShow);
    }
    public void drawShape(){
        getCroppedBitmap(Bitmap.createBitmap(btmpOrig,area_x, area_y,area_witdh, area_height));

        canvasShow.drawBitmap(btmpShadown,0, 0,null);
        canvasShow.drawBitmap(btmpAreaSegura,area_x,area_y,null);
        viewCamera.setImageBitmap(btmpShow);
    }
    public void getCroppedBitmap(Bitmap bitmap) {
        Canvas canvas = new Canvas(btmpAreaSegura);
        canvas.drawARGB(0, 0, 0, 0);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawCircle(area_witdh / 2, area_height / 2,
                area_witdh / 2, paint);

        paint.setColor(Color.RED);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        canvas.drawCircle(area_witdh / 2,area_height/ 2,
                area_witdh / 2, paintInsideStroke);
    }

    public void resize(){
        rect = new Rect(0, 0, area_witdh,area_height);
        btmpAreaSegura = Bitmap.createBitmap(area_witdh, area_height, Bitmap.Config.ARGB_8888);
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);

    }

    public void setEventoContaGota(){
        viewCamera.setImageBitmap(btmpOrig);
        viewCamera.setOnTouchListener(null);
        viewCamera.setOnTouchListener(touchEvent_contaGota);
    }
    public void delEventContaGota(){
        viewCamera.setOnTouchListener(null);
        viewCamera.setOnTouchListener(touchEvent_Move);
        drawShape();
    }

    private OnTouchListener touchEvent_contaGota = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

                Log.i("touch", Integer.toString((int) event.getX()));
                int pixel = btmpOrig.getPixel((int) event.getX(), (int) event.getY());
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
                        area_x = (int)event.getX();
                        if(area_x - area_witdh/2 < 1)
                           area_x = area_witdh/2+1;
                        else if(area_x + area_witdh/2 > viewCamera.getWidth())
                            area_x = viewCamera.getWidth() - 1 - area_witdh/2;
                        area_y = (int)event.getY();
                        if(area_y - area_height/2 < 1)
                            area_y = area_height/2 +1;
                        else if(area_y + area_height/2 > viewCamera.getHeight())
                            area_y = viewCamera.getHeight() - 1 - area_height/2;

                        Log.i("GetW",Float.toString(viewCamera.getWidth()));
                        Log.i("GetH",Float.toString(viewCamera.getHeight()));
                        Log.i("GetX",Float.toString(area_x));
                        Log.i("GetY",Float.toString(area_y));
                        drawShape();
                    }else if(move_mode == MOVE_ZOOM && event.getPointerCount() == 2){
                        float newDist = spacing(event);
                        if (newDist > 10f) {
                            float scale = newDist - oldDist;
                            float tot = scale - oldScale;
                            if(tot > 10 || tot < -10){
                                area_witdh += tot;
                                area_height += tot;
                                area_x -= tot/2;
                                area_y -= tot/2;
                                oldScale = scale;
                                resize();
                                drawShape();
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
