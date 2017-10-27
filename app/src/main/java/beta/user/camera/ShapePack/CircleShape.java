package beta.user.camera.ShapePack;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class CircleShape extends Shapes {
    private final Paint paint1 = new Paint();
    private final Paint paint2 = new Paint();
    public CircleShape(Bitmap btmp){
        super(btmp);
        paint1.setAntiAlias(true);
        paint2.setAntiAlias(true);
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }
    @Override
    public void drawShapeFormat() {
        canAreaS.drawARGB(0, 0, 0, 0);
        canAreaS.drawCircle(width / 2, height / 2,
                width / 2, paint1);

        canAreaS.drawBitmap(createTemplateAreaS(), rect, rect, paint2);

        canAreaS.drawCircle(width / 2,height/ 2,
                width / 2, paintInsideStroke);
    }

    @Override
    public void resizeFormat() {

    }
}
