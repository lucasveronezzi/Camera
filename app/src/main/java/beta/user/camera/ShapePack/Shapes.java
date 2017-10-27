package beta.user.camera.ShapePack;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Lucas on 20/10/2017.
 */

public abstract class Shapes extends Canvas{
    public float x = 200;
    public float y = 200;
    public float center;
    public float width = 150;
    public float height = 150;

    public abstract void drawShapeFormat();
    public abstract void resizeFormat();

    public Rect rect;
    public Paint paintInsideStroke = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Bitmap btmpShadown;
    private Bitmap btmpOrig;
    private Bitmap btmpShow;
    private Bitmap btmpAreaS;

    public Canvas canAreaS;

    public Shapes(Bitmap btmp){
        paintInsideStroke.setPathEffect(new DashPathEffect(new float[] {25,10}, 0));
        paintInsideStroke.setStrokeWidth(5f);
        paintInsideStroke.setStyle(Paint.Style.STROKE);
        paintInsideStroke.setColor(Color.RED);
        paintInsideStroke.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        canAreaS = new Canvas();
        updateBitmap(btmp);
        resize();
    }

    public void drawShape(){
        if(x < 0) x = 0 ;
        else if(x +  width > btmpOrig.getWidth())
            x = btmpOrig.getWidth() - width;

        if(y < 0) y = 0;
        else if(y + height >  btmpOrig.getHeight())
            y = btmpOrig.getHeight() - height;

        drawBitmap(btmpShadown,0, 0,null);
        drawShapeFormat();
        drawBitmap(btmpAreaS,x,y,null);
    }

    public void resize(){
        resizeFormat();
        rect = new Rect(0, 0, (int)width,(int)height);
        btmpAreaS = Bitmap.createBitmap((int)width, (int)height, Bitmap.Config.ARGB_8888);
        canAreaS.setBitmap(btmpAreaS);
    }

    public Bitmap createTemplateAreaS(){
        return Bitmap.createBitmap(btmpOrig,(int)x, (int)y,(int)width, (int)height);
    }

    public void updateBitmap(Bitmap btmp){
        btmpOrig = btmp;
        btmpShadown = btmpOrig.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(btmpShadown);
        RectF outerRectangle = new RectF(0, 0, btmpShadown.getWidth(), btmpShadown.getHeight());
        Paint paintOuside = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintOuside.setColor(Color.BLACK);
        paintOuside.setAlpha(64);
        canvas.drawRect(outerRectangle, paintOuside);
        btmpShow = btmpShadown.copy(Bitmap.Config.ARGB_8888, true);
        setBitmap(btmpShow);
    }

    public Bitmap getBtmpShow(){
        return btmpShow;
    }
    public Bitmap getBtmpOrig() {
        return btmpOrig;
    }
}