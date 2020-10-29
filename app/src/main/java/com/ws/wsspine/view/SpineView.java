package com.ws.wsspine.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * spine 加载类
 */
public class SpineView extends FrameLayout {
    private Context mContext;
    private int w;
    private int h;
    public SpineView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        w = getWidth();
        h = getHeight();
    }

    public void setSpineRes(){

    }
}
