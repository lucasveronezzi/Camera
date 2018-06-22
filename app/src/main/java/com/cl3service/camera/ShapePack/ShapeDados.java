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

    public boolean exposure_auto = false;
    private float exposure;

    public boolean balance_auto = true;
    public int balance_temperature_value = 4000;

    public float limit_width;
    public float limit_height;
    public float min_width;
    public float min_height;

    public int img_width = 480;
    public int img_height = 752;

    private Bitmap imgOri;
    private Bitmap imgMask;

    public int[] cor;
    public int cor_x = -1;
    public int cor_y = -1;

    private String config;

    public ShapeDados(Bitmap imgOri, Bitmap imgMask, float density, String config){
        if(imgOri == null) createImage();
        else{
            setImgOri(imgOri);
            setImgMask(imgMask);
        }
        this.density = BigDecimal.valueOf(density);

        String[] conf = config.split("\\r?\\n");

        String sCor = conf[6].split(":")[1];
        this.cor = new int[]{
                255,
                Integer.parseInt(sCor.split(",")[0]),
                Integer.parseInt(sCor.split(",")[1]),
                Integer.parseInt(sCor.split(",")[2])};

        limit_width = getScaledPixel( Integer.parseInt(conf[1].split(":")[1]) );
        limit_height = getScaledPixel( Integer.parseInt(conf[0].split(":")[1]) );

        int width_pre = Integer.parseInt(conf[5].split(":")[1]);
        x = getScaledPixel(img_width - width_pre - Integer.parseInt(conf[3].split(":")[1]) );
        y = getScaledPixel( Integer.parseInt(conf[2].split(":")[1]) );

        width = getScaledPixel(width_pre);
        height = getScaledPixel( Integer.parseInt(conf[4].split(":")[1]) );

        balance_temperature_value = Integer.parseInt(conf[11].split(":")[1]);
        if(balance_temperature_value >= 0){
            balance_auto = false;
        }
        else{
            balance_auto = true;
            this.balance_temperature_value = 2500;
        }

        gain = Float.parseFloat(conf[9].split(":")[1]);
        if(gain >= 0){
            gain_auto = false;
        }
        else{
            gain_auto = true;
            gain = 0;
        }

        exposure = Float.parseFloat(conf[10].split(":")[1]);
        if(exposure >= 0){
            exposure_auto = false;
        }
        else{
            exposure_auto = true;
            exposure = 100f;
        }

        img_width = Math.round(getScaledPixel(img_width));
        img_height = Math.round(getScaledPixel(img_height));

        set_alturaCam ( Float.parseFloat(conf[8].split(":")[1]) );

        setConfig(config);
    }

    private void createImage(){
        imgOri = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(imgOri);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);
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

    public void setImgMask(Bitmap img){
        imgMask = rotate(img, 90);
    }

    public void setImgOri(Bitmap img){
        imgOri = rotate(img, 90);
    }

    public Bitmap getImgMask(){
        return imgMask;
    }

    public Bitmap getImgOri(){
        return imgOri;
    }

    public void set_alturaCam(float value){
        alturaCam = value;
        multiCmPx = conversor.multiply( BigDecimal.valueOf(alturaCam), MathContext.DECIMAL32 );

        min_width = convertToPixel(5f);
        min_height = convertToPixel(5f);

        if(min_width > width){
            width = min_width;
        }
        if(min_height > height){
            height = min_height;
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

    public float getExpoMaxMS(){
        double fps = getFpsMax() * 0.85d;
        return (float) (1000 / fps);
    }

    public double getFpsMax(){
        float fwidth = getOriginalPixel(height);
        float fheight = getOriginalPixel(width);
        fwidth = fwidth < 96 ? 96 : fwidth;
        fheight = fheight < 64 ? 64 : fheight;
        double MHB = 61, MTW = 701, VB = 45;
        double HB = (MHB > (MTW-fwidth)) ? (MHB) : (MTW-fwidth);
        double TP = (HB + fwidth) * (VB + fheight);
        return 42682500d / TP;
    }
    public float getExpoMs(){
        float value = exposure / 1000;
        float maxValue = getExpoMaxMS() - 1;
        return value > maxValue ? maxValue : value;
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

    public void setCorHsvSave(){
        cor_x = -1;
        cor_y = -1;
    }
    public boolean check_save_config(String str){
        return !config.contentEquals(str);
    }

    public void setConfig(String str){
        String[] conf = str.split("\\r?\\n");
        String hsv = conf[7];
        config = str.replace(hsv, "cor_hsv:-1,-1");
    }
}
