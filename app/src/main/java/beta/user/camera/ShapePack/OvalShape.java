package beta.user.camera.ShapePack;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

public class OvalShape extends Shapes {
    private final Paint paint1 = new Paint();
    private final Paint paint2 = new Paint();
    public OvalShape(ShapeDados dados) {
        super(dados);
        paint1.setAntiAlias(true);
        paint2.setAntiAlias(true);
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    @Override
    public void drawShapeFormat() {
        canAreaS.drawARGB(0, 0, 0, 0);

        RectF rectF = new RectF(0,0,100,150);

        canAreaS.drawOval(rectF,paint1);

        canAreaS.drawBitmap(createTemplateAreaS(), rect, rect, paint2);

        canAreaS.drawOval(rectF, paintInsideStroke);
    }

    @Override
    public void resizeFormat(float x, float y) {

    }
}
