package com.ws.wsspine.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.SpineViewController;
import com.ws.wsspine.R;

import com.ws.wsspine.model.SpineModelLocal;


public class MumuHuanActivity extends AppCompatActivity {

    SpineModelLocal dragon;
    View dragonView;
    ViewGroup flContainer;

    int viewSizeHeight;
    int viewSizeWidth;

    SpineViewController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);
        flContainer = findViewById(R.id.fl_container);
        viewSizeWidth = getScreenWidth()  - 300;
        viewSizeHeight = viewSizeWidth + 300;

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.r = cfg.g = cfg.b = cfg.a = 8;
        cfg.useTextureView = true;
        dragon = new SpineModelLocal("mimei2/mimei.atlas" ,"mimei2/mimei.json", viewSizeWidth, viewSizeHeight);
        controller = new SpineViewController(this.getApplication());
        dragonView = controller.initializeForView(dragon, cfg);
        addDragon();
    }

    public void addDragon() {
        flContainer.addView(dragonView, 0, new ViewGroup.LayoutParams(viewSizeWidth, viewSizeHeight));
    }



    public void onClick(View view) {
        if(dragon == null){
            return;
        }
        if(view.getId() == R.id.button1){
            dragon.setSkin(dragon.getNextSkinName());
        }else  if(view.getId() == R.id.button2){
            dragon.setAnimation(dragon.getNextAnimationName());
        }else if(view.getId() == R.id.button3){
            controller.release();
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

    @Override
    protected void onPause() {
        if (controller != null) {
            controller.release();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
