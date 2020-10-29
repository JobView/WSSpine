package com.ws.wsspine.activity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AppActivity;
import com.badlogic.gdx.backends.android.SpineViewController;
import com.ws.wsspine.R;
import com.ws.wsspine.model.MumuHuan;
import com.ws.wsspine.model.SpineBody;


public class SpinebodyActivity extends AppCompatActivity {

    SpineBody dragon;
    View dragonView;
    ViewGroup flContainer;

    String [] skins = new String[]{ "loqun", "xiuxianqun"};
    int indexSkins = 0;

    int viewSizeHeight;
    int viewSizeWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);
        flContainer = findViewById(R.id.fl_container);
        viewSizeWidth = (int) (getScreenWidth() * 1);
        viewSizeHeight = dip2px(200);

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.r = cfg.g = cfg.b = cfg.a = 8;
        dragon = new SpineBody(viewSizeWidth, viewSizeHeight);
//        dragonView = initializeForView(dragon, cfg);
        dragonView = new SpineViewController(this.getApplicationContext()).initializeForView(dragon, cfg);
        dragonView.setBackgroundColor(0xFF816A17);

        if (dragonView instanceof SurfaceView) {
            SurfaceView glView = (SurfaceView) dragonView;
            glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            glView.setZOrderOnTop(true);
        }
        addDragon(true);

        addSecond();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 100);
    }

    private void addSecond() {
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.r = cfg.g = cfg.b = cfg.a = 8;
        MumuHuan dragon = new MumuHuan(viewSizeWidth, viewSizeHeight);
        dragonView = new SpineViewController(getApplicationContext()).initializeForView(dragon, cfg);
        dragonView.setBackgroundColor(0xFF814317);

        if (dragonView instanceof SurfaceView) {
            SurfaceView glView = (SurfaceView) dragonView;
            glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            glView.setZOrderOnTop(true);
        }
        addDragon(false);
    }

    public void addDragon(boolean setClick) {
        flContainer.addView(dragonView, new ViewGroup.LayoutParams(viewSizeWidth, viewSizeHeight));
        if(setClick){
            dragon.setTouchEvent(dragonView);
        }

    }



    public void onClick(View view) {
        if(view.getId() == R.id.button1){
            indexSkins = (++indexSkins) % skins.length;
            dragon.setSkin(skins[indexSkins]);
        }else  if(view.getId() == R.id.button2){
            dragon.changeAnimation();
        }

    }


    public  int dip2px(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5F);
    }


    public  int getScreenWidth() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return  outMetrics.widthPixels;
    }

}
