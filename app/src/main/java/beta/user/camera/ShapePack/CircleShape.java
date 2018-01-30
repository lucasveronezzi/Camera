package beta.user.camera.ShapePack;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class CircleShape extends Shapes {
    private final Paint paint1 = new Paint();
    private final Paint paint2 = new Paint();
    public CircleShape(ShapeDados dados){
        super(dados);
        paint1.setAntiAlias(true);
        paint2.setAntiAlias(true);
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }
    @Override
    public void drawShapeFormat() {
        canAreaS.drawARGB(0, 0, 0, 0);
        canAreaS.drawCircle(dados.width / 2, dados.height / 2,
                dados.width / 2, paint1);

        canAreaS.drawBitmap(createTemplateAreaS(), rect, rect, paint2);

        canAreaS.drawCircle(dados.width / 2,dados.height/ 2,
                dados.width / 2, paintInsideStroke);
    }

    @Override
    public void resizeFormat(float x,float y) {
        float tot = x + y;
        if (tot > 10 || tot < -10) {
            dados.width += tot;
            dados.height += tot;
            if (dados.width > getBtmpOrig().getWidth()) {
                dados.width = getBtmpOrig().getWidth();
            } else if (dados.width < 50f) {
                dados.width = 50f;
            }
            if (dados.height > getBtmpOrig().getHeight()) {
                dados.height = getBtmpOrig().getHeight();
            } else if (dados.height < 50f) {
                dados.height = 50f;
            }

            dados.x -= tot/2;
            dados.y -= tot/2;
        }
        resize();
    }
}
