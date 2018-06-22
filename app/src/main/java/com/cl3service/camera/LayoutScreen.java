package com.cl3service.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;

import com.cl3service.camera.ShapePack.CircleShape;
import com.cl3service.camera.ShapePack.RecShape;
import com.cl3service.camera.ShapePack.ShapeDados;
import com.cl3service.camera.ShapePack.Shapes;

public class LayoutScreen extends RelativeLayout {
    private FloatingActionMenu fbMenu;
    private Rect fabRect;

    private ImageView viewCamera;

    static final int MOVE_NONE = 0;
    static final int MOVE_DRAG = 1;
    static final int MOVE_RESIZE = 2;

    public int idShape = 0;
    private int move_mode = MOVE_NONE;
    private float oldX = 0;
    private float oldY = 0;
    public RelativeLayout backColorSeguranca;

    private double scaleX;
    private double scaleY;
    private boolean firstImage = true;

    private File dir;

    private Shapes shape;

    public LayoutScreen(Context context) {
        super(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(lp);

        LayoutInflater inflater = LayoutInflater.from(context);
        View inflatedLayout= inflater.inflate(R.layout.activity_camera, null, false);
        addView(inflatedLayout);

        backColorSeguranca = (RelativeLayout)findViewById(R.id.corArea_view);
        fbMenu = (FloatingActionMenu)findViewById(R.id.fab_menu);
        ( (FloatingActionButton)findViewById(R.id.size_area) ).setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_size_select_large_black_24dp));
        ( (FloatingActionButton)findViewById(R.id.conta_gota) ).setImageDrawable(getResources().getDrawable(R.drawable.ic_conta_gota));
        ( (FloatingActionButton)findViewById(R.id.testar_imagem) ).setImageDrawable(getResources().getDrawable(R.drawable.ic_testar_imagem_black_24dp));
        ( (FloatingActionButton)findViewById(R.id.atualizar) ).setImageDrawable(getResources().getDrawable(R.drawable.ic_linked_camera_black_24dp));
        ( (FloatingActionButton)findViewById(R.id.salvar) ).setImageDrawable(getResources().getDrawable(R.drawable.ic_save_black_24dp));


        fabRect = new Rect();
        viewCamera = new ImageView(context);
        inicialize();
    }

    private void inicialize(){
        viewCamera.setLayoutParams(
                new ViewGroup.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        viewCamera.setAdjustViewBounds(true);
        ((LinearLayout)findViewById(R.id.layoutImage)).addView(viewCamera);

        fbMenu.bringToFront();
    }

    public void hideFab(){
        if(fbMenu.isShown()){
            fbMenu.close(true);
        }
    }

    private void updateImage(){
        shape.drawShape();
        setImgView();
    }

    public void setImgView(){
        viewCamera.setImageBitmap(shape.getBtmpShow());
        viewCamera.invalidate();
    }

    public void newImage(){
        firstImage = true;
        shape.updateBitmap();
        shape.drawShape();
    }

    public void setAreaAtCm(float widthCm, float heightCm){
        shape.resizeFormatAtCm(widthCm, heightCm);
        updateImage();
    }

    public void setShape(int id, ShapeDados dados){
        switch (id){
            case 0:
                shape = new CircleShape(dados);
                //((FloatingActionButton)findViewById(R.id.formato)).setImageResource(R.mipmap.ic_circle);
                break;
            case 1:
                shape = new RecShape(dados);
                //((FloatingActionButton)findViewById(R.id.formato)).setImageResource(R.mipmap.ic_rect);
                break;
        }
        updateColorSeg(shape.dados.cor);
        delEventContaGota();
    }

    public ShapeDados getDados(){
        return shape.dados;
    }

    public Bitmap getBitmapMask(){
        return shape.getBtmpMask(shape.dados.getImgOri().getWidth(), shape.dados.getImgOri().getHeight());
    }

    public void setEventoContaGota(){
        viewCamera.setImageBitmap( shape.dados.getImgOri() );
        viewCamera.setOnTouchListener(null);
        viewCamera.setOnTouchListener(touchEvent_contaGota);
    }
    public void delEventContaGota(){
        viewCamera.setOnTouchListener(null);
        viewCamera.setOnTouchListener(touchEvent_Move);
    }

    public void updateColorSeg(int[] cor){
        backColorSeguranca.setBackgroundColor(Color.argb(cor[0],cor[1],cor[2],cor[3]));
        backColorSeguranca.invalidate();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                    if (CameraActivity.flag_contaGota) {
                        Rect viewRect = new Rect();
                        viewCamera.getGlobalVisibleRect(viewRect);
                        if (!viewRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                            delEventContaGota();
                            updateImage();
                            CameraActivity.flag_contaGota = false;
                        }
                    }
                break;

        }
        return false;
    }

    public void setScaleImage(){
        scaleX = (double)shape.dados.getImgOri().getWidth() / (double)viewCamera.getWidth();
        scaleY = (double)shape.dados.getImgOri().getHeight() / (double)viewCamera.getHeight();
        firstImage = false;
    }

    private OnTouchListener touchEvent_contaGota = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(firstImage) setScaleImage();

            shape.dados.saveDados = true;
            shape.dados.cor_x = (int)((double)event.getX() * scaleX);
            shape.dados.cor_y = (int)((double)event.getY() * scaleY);

            /*float[] eventXY = new float[] {event.getX(), event.getY()};
            Matrix invertMatrix = new Matrix();
            viewCamera.getImageMatrix().invert(invertMatrix);
            invertMatrix.mapPoints(eventXY);
            int x = Integer.valueOf((int)eventXY[0]);
            int y = Integer.valueOf((int)eventXY[1]);*/

            int pixel = shape.dados.getImgOri().getPixel(shape.dados.cor_x, shape.dados.cor_y);
            shape.dados.cor[0] = Color.alpha(pixel);
            shape.dados.cor[1] = Color.red(pixel);
            shape.dados.cor[2] = Color.green(pixel);
            shape.dados.cor[3] = Color.blue(pixel);
            updateColorSeg(shape.dados.cor);
            delEventContaGota();
            updateImage();
            return false;
        }
    };

    private OnTouchListener touchEvent_Move = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(firstImage)  setScaleImage();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    hideFab();
                    oldX = event.getX();
                    oldY = event.getY();
                    move_mode = MOVE_DRAG;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldX  = Math.abs(event.getX(0) - event.getX(1));
                    oldY = Math.abs(event.getY(0) - event.getY(1));
                    if (oldX > 10f || oldY > 10f) {
                        move_mode = MOVE_RESIZE;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(move_mode == MOVE_DRAG){

                        shape.dados.x = shape.dados.x + (float) ( (event.getX() - oldX) * scaleX );
                        shape.dados.y = shape.dados.y + (float) ((event.getY() - oldY) * scaleY);

                        oldX = event.getX();
                        oldY = event.getY();
                        updateImage();
                    }else if(move_mode == MOVE_RESIZE && event.getPointerCount() == 2){
                                shape.resizeFormat(
                                        (float) ( ( Math.abs(event.getX(0) - event.getX(1)) - oldX ) * scaleX ),
                                        (float) ( ( Math.abs(event.getY(0) - event.getY(1)) - oldY ) * scaleY )
                                );

                                oldX = Math.abs(event.getX(0) - event.getX(1));
                                oldY = Math.abs(event.getY(0) - event.getY(1));
                                updateImage();
                    }
                    break;
                case MotionEvent.ACTION_UP: //first finger lifted
                case MotionEvent.ACTION_POINTER_UP: //second finger lifted
                    move_mode = MOVE_NONE;
                    break;
            }
            return true;
        }

    };
}
