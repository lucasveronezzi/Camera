package beta.user.camera.ShapePack;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

/**
 * Created by User on 25/10/2017.
 */

public class OvalShape extends Shapes {
    public OvalShape(Bitmap btmp) {
        super(btmp);
    }

    @Override
    public void drawShapeFormat() {
        canAreaS.drawARGB(0, 0, 0, 0);

        RectF rectF = new RectF(0,0,100,150);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        canAreaS.drawOval(rectF,paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canAreaS.drawBitmap(createTemplateAreaS(), rect, rect, paint);

        canAreaS.drawOval(rectF, paintInsideStroke);
    }
}
