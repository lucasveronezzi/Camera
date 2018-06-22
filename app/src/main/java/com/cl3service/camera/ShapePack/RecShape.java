package com.cl3service.camera.ShapePack;

import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by User on 25/10/2017.
 */

public class RecShape extends Shapes {
    private final Paint pain1 = new Paint();
    public RecShape(ShapeDados dados) {
        super(dados);
        pain1.setAntiAlias(true);
    }

    @Override
    public void drawShapeFormat() {
        rect = new Rect( (int)dados.x, (int)dados.y, (int) (dados.x + dados.width),(int) (dados.y + dados.height) );
        drawBitmap( dados.getImgOri(), rect, rect, pain1);
        drawRect(dados.x, dados.y, dados.x + dados.width, dados.y + dados.height, paintInsideStroke);
    }

    @Override
    public void resizeFormat(float x, float y) {
            dados.width += x;
            if (dados.width > dados.limit_width) {
                dados.width = dados.limit_width;
            } else if (dados.width < dados.min_width) {
                dados.width = dados.min_width;
            }
            dados.x -= x/2;
            if(dados.x < 0) dados.x = 0 ;

            dados.height += y;
            if (dados.height > dados.limit_height) {
                dados.height = dados.limit_height;
            } else if (dados.height < dados.min_height) {
                dados.height = dados.min_height;
            }
            dados.y -= y/2;
            if(dados.y < 0) dados.y = 0;
    }

    @Override
    public void resizeFormatAtCm(float widthCm, float heightCm) {
        dados.width =  dados.convertToPixel(widthCm);
        dados.height = dados.convertToPixel(heightCm);
    }
}
