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


public class MultipleSpineActivity extends AppCompatActivity {

    SpineModelLocal dragon;
    View dragonView;
    ViewGroup flContainer1, flContainer2, flContainer3, flContainer4;

    int viewSizeHeight;
    int viewSizeWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple);
        flContainer1 = findViewById(R.id.fl_container1);
        flContainer2 = findViewById(R.id.fl_container2);
        flContainer3 = findViewById(R.id.fl_container3);
        flContainer4 = findViewById(R.id.fl_container4);
        viewSizeWidth = getScreenWidth() / 2;
        viewSizeHeight = getScreenHeight() / 2;

        addDragon(flContainer1);
        flContainer2.postDelayed(new Runnable() {
            @Override
            public void run() {
                addDragon(flContainer2);
            }
        }, 1000);

        flContainer3.postDelayed(new Runnable() {
            @Override
            public void run() {
                addDragon(flContainer3);
            }
        }, 2000);

        flContainer4.postDelayed(new Runnable() {
            @Override
            public void run() {
                addDragon(flContainer4);
            }
        }, 3000);

//        addDragon(flContainer2);
//        addDragon(flContainer3);
//        addDragon(flContainer4);
    }

    public void addDragon(ViewGroup parent) {
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.r = cfg.g = cfg.b = cfg.a = 8;
        cfg.useTextureView = true;
        dragon = new SpineModelLocal("mimei2/mimei.atlas" ,"mimei2/mimei.json", viewSizeWidth, viewSizeHeight);
//        dragon = new SpineModelLocal("spineboy/spineboy-pma.atlas" ,"spineboy/spineboy-ess.json", viewSizeWidth, viewSizeHeight);
        dragonView = new SpineViewController(this.getApplication()).initializeForView(dragon, cfg);
        parent.addView(dragonView, 0, new ViewGroup.LayoutParams(viewSizeWidth, viewSizeHeight));
    }



    public void onClick(View view) {
        if(dragon == null){
            return;
        }
        if(view.getId() == R.id.button1){
            dragon.setSkin(dragon.getNextSkinName());
        }else  if(view.getId() == R.id.button2){
            dragon.setAnimation(dragon.getNextAnimationName());
        }

    }




    public  int getScreenWidth() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return  outMetrics.widthPixels;
    }

    public  int getScreenHeight() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return  outMetrics.heightPixels - 250;
    }

}
