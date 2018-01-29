package beta.user.camera.ShapePack;

import android.graphics.Bitmap;

/**
 * Created by Lucas on 23/01/2018.
 */

public class ShapeDados {
    public float x;
    public float y;
    public float width;
    public float height;

    public Bitmap img;

    public int[] cor = new int[4];

    public ShapeDados(Bitmap img, int[] cor, int x, int y, int width, int height){
        this.img = img;
        this.cor = cor;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
