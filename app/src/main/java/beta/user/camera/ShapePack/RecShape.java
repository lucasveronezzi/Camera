package beta.user.camera.ShapePack;

import android.graphics.Bitmap;
import android.graphics.Paint;

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
        canAreaS.drawARGB(0, 0, 0, 0);
        canAreaS.drawBitmap(createTemplateAreaS(), rect, rect, pain1);
        canAreaS.drawRect(rect,paintInsideStroke);
    }

    @Override
    public void resizeFormat(float x, float y) {

    }
}
