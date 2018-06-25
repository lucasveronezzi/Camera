package com.cl3service.camera.ShapePack;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.cl3service.camera.CameraActivity;

import java.math.BigDecimal;

/**
 * Created by Lucas on 20/10/2017.
 */

public abstract class Shapes extends Canvas{
    public ShapeDados dados;

    public abstract void drawShapeFormat();
    public abstract void resizeFormat(float x, float y);
    public abstract void resizeFormatAtCm(float widthCm, float heightCm);

    public Rect rect;
    public Paint paintInsideStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paintText;
    private Paint paintRecInfo;
    private Paint paintShadown;

    private Bitmap btmpShadown;
    private Bitmap btmpShow;

    private float yRecTxt;
    private float xRecTxt;
    private float widthTxtL;
    private float widthTxtC;
    private float xRightRect;
    private float yBottomRect;

    private int fonte_sizePixel;
    private final int marginTextInfo = 10;

    public Canvas canAreaS;

    public Shapes(ShapeDados dados){
        this.dados = dados;

        paintText = new Paint();
        paintText.setColor(Color.WHITE);
        // The gesture threshold expressed in dp
        float fonte_sizeDP = 20.0f;
        fonte_sizePixel = (int) (fonte_sizeDP * dados.density.floatValue() + 0.5f);
        // Convert the dps to pixels, based on density scale
        paintText.setTextSize(fonte_sizePixel);

        paintRecInfo = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintRecInfo.setColor(Color.BLACK);
        paintRecInfo.setAlpha(90);

        paintInsideStroke.setPathEffect(new DashPathEffect(new float[] {25,10}, 0));
        paintInsideStroke.setStrokeWidth(5f);
        paintInsideStroke.setStyle(Paint.Style.STROKE);
        paintInsideStroke.setColor(Color.RED);
        paintInsideStroke.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        paintShadown = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintShadown.setAlpha(80);

        canAreaS = new Canvas();
    }

    public void drawShape(){
        if(dados.x < 0) dados.x = 0 ;
        else if(dados.x + dados.width > dados.getImgOri().getWidth())
            dados.x = dados.getImgOri().getWidth() - dados.width;

        if(dados.y < 0) dados.y = 0;
        else if(dados.y + dados.height >  dados.getImgOri().getHeight())
            dados.y = dados.getImgOri().getHeight() - dados.height;

        drawBitmap(dados.getImgOri(),0, 0,null);
        drawBitmap(btmpShadown,0, 0,paintShadown);
        drawShapeFormat();
        drawInfo();
    }

    public void drawInfo(){
        float l = dados.convertToCm(dados.width);
        float a = dados.convertToCm(dados.height);

        String txtL = "L: " + String.format("%.2f", l) + " cm";
        String textC = "C: " + String.format("%.2f", a) + " cm";

        widthTxtL =  paintText.measureText(txtL);
        widthTxtC =  paintText.measureText(textC);

        xRecTxt =  dados.x + dados.width + 10;
        yRecTxt = dados.y;

        xRightRect = xRecTxt + (20 * 2) + (widthTxtL > widthTxtC ? widthTxtL : widthTxtC);
        yBottomRect = dados.y + (fonte_sizePixel * 2) + (marginTextInfo * 5);

        if(xRightRect > dados.getImgOri().getWidth()){
            xRecTxt = dados.x - 10 - (xRightRect - xRecTxt);
            xRightRect = xRecTxt + (20 * 2) + (widthTxtL > widthTxtC ? widthTxtL : widthTxtC);
        }

        if(yBottomRect > dados.getImgOri().getHeight()){
            yRecTxt =  dados.getImgOri().getHeight() - (yBottomRect - yRecTxt);
            yBottomRect = dados.getImgOri().getHeight();
        }

        RectF rectInfo = new RectF(xRecTxt, yRecTxt, xRightRect, yBottomRect);
        drawRoundRect(rectInfo, 15, 15, paintRecInfo);

        drawText(txtL, xRecTxt + 20, yRecTxt + fonte_sizePixel + marginTextInfo, paintText);
        drawText(textC, xRecTxt + 20, yRecTxt + (fonte_sizePixel * 2) + (marginTextInfo * 2), paintText);
    }

    public void updateBitmap(){
        createShadownMask(dados.getImgOri().getWidth(), dados.getImgOri().getHeight());
        btmpShow = dados.getImgOri().copy(Bitmap.Config.ARGB_8888, true);
        setBitmap(btmpShow);
    }

    public void createShadownMask(int width, int height){
        btmpShadown = Bitmap.createBitmap( width, height, Bitmap.Config.ALPHA_8);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        Canvas canvas = new Canvas(btmpShadown);
        canvas.drawRect(0, 0, width, height, paint);
    }

    public Bitmap getBtmpMask(int width, int height){
        Bitmap btmp = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);

        Canvas canvas = new Canvas(btmp);
        canvas.drawRect(0, 0, width, height, paint);
        canvas.drawBitmap(dados.getImgMask(), dados.x, dados.y, null);
        canvas.drawRect(dados.x, dados.y, dados.x + dados.width, dados.y + dados.height, paintInsideStroke);
        return btmp;
    }

    public Bitmap getBtmpMask2(int width, int height){
        Bitmap btmp = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);

        Canvas canvas = new Canvas(btmp);
        canvas.drawRect(0, 0, width, height, paint);
        canvas.drawBitmap(dados.getImgMask2(), dados.x, dados.y, null);
        canvas.drawRect(dados.x, dados.y, dados.x + dados.width, dados.y + dados.height, paintInsideStroke);
        return btmp;
    }

    public Bitmap getBtmpShow(){
        return btmpShow;
    }
}