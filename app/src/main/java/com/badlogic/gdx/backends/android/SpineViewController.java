package com.badlogic.gdx.backends.android;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.Skin;
import com.ws.wsspine.model.SpineModelLocal;

import java.util.HashMap;
import java.util.Map;

public class SpineViewController extends WSSpineBaseApplication {
    public static Map<ApplicationListener, Graphics> Graphics = new HashMap();


    public SpineViewController(Context context){
        super(context);
    }


    public View initializeForView(ApplicationListener listener, AndroidApplicationConfiguration config) {
        this.init(listener, config);
        return this.graphics.getView();
    }

    private void init(ApplicationListener listener, AndroidApplicationConfiguration config) {
        this.graphics = new AndroidGraphics(this, config, (ResolutionStrategy) (config.resolutionStrategy == null ? new FillResolutionStrategy() : config.resolutionStrategy));
        Graphics.put(listener, this.graphics);
        this.input = AndroidInputFactory.newAndroidInput(this, mContext, this.graphics.view, config);
        this.audio = new AndroidAudio(mContext, config);
        this.files = new AndroidFiles(mContext.getAssets(), mContext.getFilesDir().getAbsolutePath());
        this.net = new AndroidNet(this, config);
        this.listener = listener;
        this.handler = new Handler();
        this.useImmersiveMode = config.useImmersiveMode;
        this.hideStatusBar = config.hideStatusBar;
        this.addLifecycleListener(new LifecycleListener() {
            public void resume() {
            }

            public void pause() {
                SpineViewController.this.audio.pause();
            }

            public void dispose() {
                SpineViewController.this.audio.dispose();
            }
        });
            Gdx.app = this;
            Gdx.input = this.input;
            Gdx.audio = this.audio;
            Gdx.files = this.files;
            Gdx.graphics = this.graphics;
            Gdx.net = this.net;

    }

}

