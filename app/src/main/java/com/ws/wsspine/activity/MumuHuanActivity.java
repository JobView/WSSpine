package com.ws.wsspine.activity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AppActivity;
import com.ws.wsspine.R;
import com.ws.wsspine.model.MumuHuan;

import com.badlogic.gdx.backends.android.SpineViewHelper;


public class MumuHuanActivity extends AppCompatActivity {

    MumuHuan dragon;
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
        viewSizeHeight = dip2px(600);

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.r = cfg.g = cfg.b = cfg.a = 8;
        dragon = new MumuHuan(viewSizeWidth, viewSizeHeight);
//        dragonView = initializeForView(dragon, cfg);
        dragonView = new SpineViewHelper(this.getApplication(), getWindowManager()).initializeForView(dragon, cfg);
        dragonView.setBackgroundColor(0xFFFFFFFF);

        if (dragonView instanceof SurfaceView) {
            SurfaceView glView = (SurfaceView) dragonView;
            glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            glView.setZOrderOnTop(true);
        }
        addDragon();
    }

    public void addDragon() {
        flContainer.addView(dragonView, new ViewGroup.LayoutParams(viewSizeWidth, viewSizeHeight));
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
