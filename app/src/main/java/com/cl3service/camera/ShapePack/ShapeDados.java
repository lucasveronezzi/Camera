package com.cl3service.camera.ShapePack;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Created by Lucas on 23/01/2018.
 */

public class ShapeDados {
    public float x;
    public float y;
    public float width;
    public float height;
    private BigDecimal conversor = BigDecimal.valueOf(0.00186f);
    private float alturaCam;
    public BigDecimal multiCmPx;
    public BigDecimal density;
    public boolean saveDados = false;
    public boolean updateImg = false;

    public boolean gain_auto = true;
    public float gain;
    public float gainMin = 0;
    public float gainMax = 11.75f;

    public boolean exposure_auto = false;
    private float exposure;
    public float exposureMin = 1000f;
    public float exposureMax = 9000f;

    public boolean balance_auto = true;
    public int balance_temperature_value = 4000;

    public float limit_width;
    public float limit_height;
    public float min_width;
    public float min_height;

    public int img_width = 480;
    public int img_height = 752;

    public float x_bk;
    public float y_bk;
    public float width_bk;
    public float height_bk;

    private Bitmap imgOri;
    private Bitmap imgMask;

    public int[] cor = new int[4];
    public int cor_x = -1;
    public int cor_y = -1;

    public TypeImg imgType;

    public ShapeDados(Bitmap imgOri, Bitmap imgMask, float density, int[] cor, int x, int y, int width, int height, int limit_width, int limit_height, float altura, float gain, float exposure, int balance_temperature_value){
        if(imgOri == null) createImage();
        else{
            setImgOri(imgOri);
            if(imgMask != null)
                setImgMask(imgMask);
        }

        this.density = BigDecimal.valueOf(density);

        this.cor = cor;
        this.x = getScaledPixel(img_width - width - x);
        this.y = getScaledPixel(y);
        this.width = getScaledPixel(width);
        this.height = getScaledPixel(height);

        this.limit_width = getScaledPixel(limit_width);
        this.limit_height = getScaledPixel(limit_height);

        img_width = Math.round(getScaledPixel(img_width));
        img_height = Math.round(getScaledPixel(img_height));

        if(balance_temperature_value >= 0){
            balance_auto = false;
            this.balance_temperature_value = balance_temperature_value;
        }
        else{
            balance_auto = true;
            this.balance_temperature_value = 2500;
        }

        if(gain >= 0){
            gain_auto = false;
            this.gain = gain;
        }
        else{
            gain_auto = true;
            this.gain = gainMin;
        }

        if(exposure >= 0){
            exposure_auto = false;
            this.exposure = exposure;
        }
        else{
            exposure_auto = true;
            this.exposure = exposureMin;
        }

        set_alturaCam (altura);

        imgType = TypeImg.ImgOrig;
    }

    private void createImage(){
        imgOri = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(imgOri);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);
    }

    public Bitmap getImg(){
        if(imgType == TypeImg.ImgOrig)
            return imgOri;
        else
            return imgMask;
    }

    public void setImgMask(Bitmap img){
        this.imgMask = rotate(img, 90);
    }
    public void setImgOri(Bitmap img){
        this.imgOri = rotate(img, 90);
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

    public void set_backup(){
        x_bk = x;
        y_bk = y;
        width_bk = width;
        height_bk = height;
    }

    public void return_backup(){
        x = x_bk;
        y = y_bk;
        width = width_bk;
        height = height_bk;
    }

    public void set_alturaCam(float value){
        alturaCam = value;
        multiCmPx = conversor.multiply( BigDecimal.valueOf(alturaCam), MathContext.DECIMAL32 );

        min_width = convertToPixel(5f);
        min_height = convertToPixel(5f);

        if(min_width > width){
            width_bk = min_width;
            width = min_width;
        }
        if(min_height > height){
            height = min_height;
            height_bk = min_height;
        }
    }

    public float convertToCm(float value){
        BigDecimal bdValue = BigDecimal.valueOf(value);
        return bdValue.multiply(multiCmPx, MathContext.DECIMAL32).divide(density, MathContext.DECIMAL32).floatValue();
    }

    public float convertToPixel(float value){
        BigDecimal bdValue = BigDecimal.valueOf(value);
        return bdValue.divide(multiCmPx, MathContext.DECIMAL32).multiply(density, MathContext.DECIMAL32).floatValue();
    }

    public float getScaledPixel(float value){
        BigDecimal bdValue = BigDecimal.valueOf(value);
        return bdValue.multiply(density, MathContext.DECIMAL32).floatValue();
    }

    public float getOriginalPixel(float value){
        BigDecimal bdValue = BigDecimal.valueOf(value);
        return bdValue.divide(density, MathContext.DECIMAL32).floatValue();
    }

    public float getExpoMs(){
        return exposure / 1000;
    }
    public float getExpo(){
        return exposure;
    }
    public void setExpoMs(float ms){
        exposure = ms * 1000;
    }

    public float get_alturaCam(){
        return alturaCam;
    }

    public float get_x_bk(){
        return x_bk;
    }
    public float get_y_bk(){
        return y_bk;
    }

    public enum TypeImg{
        ImgOrig,
        ImgMask
    }

}
