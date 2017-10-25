package beta.user.camera.ShapePack;

import android.graphics.Bitmap;
import android.graphics.Paint;

/**
 * Created by User on 25/10/2017.
 */

public class RecShape extends Shapes {
    public RecShape(Bitmap btmp) {
        super(btmp);
    }

    @Override
    public void drawShapeFormat() {
        canAreaS.drawARGB(0, 0, 0, 0);
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        canAreaS.drawBitmap(createTemplateAreaS(), rect, rect, paint);
        canAreaS.drawRect(rect,paintInsideStroke);
    }
}
