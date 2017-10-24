package beta.user.camera;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.clans.fab.FloatingActionMenu;

/**
 * Created by Lucas on 21/10/2017.
 */

public class LayoutScreen extends RelativeLayout {
    private LayoutImageCamera layoutImage;
    private static FloatingActionMenu fbMenu;
    private Rect fabRect;
    public LayoutScreen(Context context) {
        super(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(lp);

        LayoutInflater inflater = LayoutInflater.from(context);
        View inflatedLayout= inflater.inflate(R.layout.activity_camera, null, false);
        addView(inflatedLayout);
    }

    public LayoutScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LayoutScreen(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void inicialize(LayoutImageCamera img){
        layoutImage = img;
        fbMenu = (FloatingActionMenu)findViewById(R.id.fab_menu);
        fabRect = new Rect();
    }

    public static void hideFab(){
        if(fbMenu.isShown()){
            fbMenu.close(true);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                    if (CameraActivity.flag_contaGota) {
                        Rect viewRect = new Rect();
                        layoutImage.getGlobalVisibleRect(viewRect);
                        if (!viewRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                            layoutImage.delEventContaGota();
                            CameraActivity.flag_contaGota = false;
                        }
                    }
                fbMenu.getGlobalVisibleRect(fabRect);
                if (fabRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {

                }
                break;

        }
        return false;
    }
}
