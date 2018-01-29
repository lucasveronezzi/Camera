package beta.user.camera.ShapePack;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Lucas on 20/10/2017.
 */

public abstract class Shapes extends Canvas{
    public ShapeDados dados;

    public abstract void drawShapeFormat();
    public abstract void resizeFormat(float x, float y);

    public Rect rect;
    public Paint paintInsideStroke = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Bitmap btmpShadown;
    private Bitmap btmpOrig;
    private Bitmap btmpShow;
    private Bitmap btmpAreaS;

    public Canvas canAreaS;

    public Shapes(ShapeDados dados){
        this.dados = dados;

        paintInsideStroke.setPathEffect(new DashPathEffect(new float[] {25,10}, 0));
        paintInsideStroke.setStrokeWidth(5f);
        paintInsideStroke.setStyle(Paint.Style.STROKE);
        paintInsideStroke.setColor(Color.RED);
        paintInsideStroke.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        canAreaS = new Canvas();
        updateBitmap(rotate(dados.img, 90));
    }

    public void drawShape(){
        if(dados.x < 0) dados.x = 0 ;
        else if(dados.x +  dados.width > btmpOrig.getWidth())
            dados.x = btmpOrig.getWidth() - dados.width;

        if(dados.y < 0) dados.y = 0;
        else if(dados.y + dados.height >  btmpOrig.getHeight())
            dados.y = btmpOrig.getHeight() - dados.height;

        drawBitmap(btmpShadown,0, 0,null);
        drawShapeFormat();
        drawBitmap(btmpAreaS,dados.x,dados.y,null);
    }

    public void resize(){
        rect = new Rect(0, 0, (int)dados.width,(int)dados.height);
        btmpAreaS = Bitmap.createBitmap((int)dados.width, (int)dados.height, Bitmap.Config.ARGB_8888);
        canAreaS.setBitmap(btmpAreaS);
    }

    public Bitmap createTemplateAreaS(){
        return Bitmap.createBitmap(btmpOrig,(int)dados.x, (int)dados.y,(int)dados.width, (int)dados.height);
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

    private Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();

            m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(
                        b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                throw ex;
            }
        }
        return b;
    }

    public Bitmap getBtmpShow(){
        return btmpShow;
    }
    public Bitmap getBtmpOrig() {
        return btmpOrig;
    }

}