package beta.user.camera.ShapePack;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

/**
 * Created by Lucas on 21/10/2017.
 */

public class CircleShape extends Shapes {

    public CircleShape(Bitmap btmp){
        super(btmp);
    }

    public void drawShape(){
        canAreaS.drawARGB(0, 0, 0, 0);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        canAreaS.drawCircle(width / 2, height / 2,
                width / 2, paint);

        paint.setColor(Color.RED);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canAreaS.drawBitmap(createTemplateAreaS(), rect, rect, paint);

        canAreaS.drawCircle(width / 2,height/ 2,
                width / 2, paintInsideStroke);
        super.drawShape();
    }
}
