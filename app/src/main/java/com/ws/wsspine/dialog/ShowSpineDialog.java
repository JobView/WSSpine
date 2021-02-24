package com.ws.wsspine.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.SpineViewController;
import com.ws.wsspine.R;
import com.ws.wsspine.Utils;
import com.ws.wsspine.model.SpineModelLocal;


public class ShowSpineDialog extends Dialog {
    public ShowSpineDialog(Context context) {
        super(context, R.style.blackDialog);
    }
     SpineModelLocal dragon;
     View dragonView;
    SpineViewController controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_spine_show);
        init(savedInstanceState);
    }

    protected void init(Bundle savedInstanceState) {
        final FrameLayout parent = findViewById(R.id.fl_spine);
        findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dragon.addAnimation(dragon.getNextAnimationName());
            }
        });
        final int viewSizeWidth = Utils.getScreenWidth(getContext()) / 2;
        final int viewSizeHeight = Utils.getScreenHeight(getContext()) / 2;
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.r = cfg.g = cfg.b = cfg.a = 8;
        cfg.useTextureView = true;
//       dragon = new SpineModelLocal("spineboy/spineboy-pma.atlas" ,"spineboy/spineboy-ess.json", viewSizeWidth, viewSizeHeight);
         dragon = new SpineModelLocal("mimei2/mimei.atlas" ,"mimei2/mimei.json", viewSizeWidth, viewSizeHeight);
         controller = new SpineViewController(this.getContext());
        dragonView = controller.initializeForView(dragon, cfg);
        parent.addView(dragonView,  new ViewGroup.LayoutParams(viewSizeWidth, viewSizeHeight));
    }

    @Override
    public void dismiss() {
        if (controller != null) {
            controller.release();
        }
        super.dismiss();
    }
}
