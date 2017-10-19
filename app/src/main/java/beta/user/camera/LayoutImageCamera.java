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
    private Paint paintInsideStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Rect rect;
    private ImageView viewCamera;
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
            rect = new Rect(0, 0, btmpOrig.getWidth(),btmpOrig.getHeight());
            setBtmpShadown();
            drawShape();
        }
        //viewCamera.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
    public void setBtmpShadown(){
        btmpShadown = btmpOrig.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(btmpShadown);
        RectF outerRectangle = new RectF(0, 0, btmpShadown.getWidth(), btmpShadown.getHeight());
        Paint paintOuside = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintOuside.setColor(Color.BLACK);
        paintOuside.setAlpha(64);
        canvas.drawRect(outerRectangle, paintOuside);
    }
    public void drawShape(){
        btmpAreaSegura = Bitmap.createBitmap(btmpOrig.getWidth(), btmpOrig.getHeight(), Bitmap.Config.ARGB_8888);
        getCroppedBitmap(btmpOrig);

        Bitmap btmp = btmpShadown.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(btmp);
        canvas.drawBitmap(btmpAreaSegura,0,0,null);
        viewCamera.setImageBitmap(btmp);
    }

    public void getCroppedBitmap(Bitmap bitmap) {
        Canvas canvas = new Canvas(btmpAreaSegura);
        canvas.drawARGB(0, 0, 0, 0);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xff424242);

        canvas.drawCircle(400 / 2, 400 / 2,
                200 / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        canvas.drawCircle(400 / 2,400/ 2,
               200 / 2, paintInsideStroke);
    }


    public void setEventoContaGota(){
        viewCamera.setImageBitmap(btmpOrig);
        viewCamera.setOnTouchListener(touchEvent_contaGota);
    }
    public void delEventContaGota(){
        viewCamera.setOnTouchListener(null);
        drawShape();
    }

    private OnTouchListener touchEvent_contaGota = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.i("touch", Integer.toString((int) event.getX()));
                int pixel = btmpOrig.getPixel((int) event.getX(), (int) event.getY());
                cor_areaSegura[0] = Color.alpha(pixel);
                cor_areaSegura[1] = Color.red(pixel);
                cor_areaSegura[2] = Color.green(pixel);
                cor_areaSegura[3] = Color.blue(pixel);
                backColorSeguranca.setColor(pixel);
                delEventContaGota();

            }
            return false;
        }
    };
}
