package com.badlogic.gdx.backends.android;

import android.app.Activity;
import android.os.Handler;
import android.view.View;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;
import com.ws.wsspine.WSSpineBaseApplication;

public class SpineViewHelper extends WSSpineBaseApplication {

    public SpineViewHelper(Activity context){
        this.mContext = context;
    }


    public View initializeForView(ApplicationListener listener, AndroidApplicationConfiguration config) {
        this.init(listener, config);
        return this.graphics.getView();
    }

    private void init(ApplicationListener listener, AndroidApplicationConfiguration config) {
        this.graphics = new AndroidGraphics(this, config, (ResolutionStrategy) (config.resolutionStrategy == null ? new FillResolutionStrategy() : config.resolutionStrategy));
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
                SpineViewHelper.this.audio.pause();
            }

            public void dispose() {
                SpineViewHelper.this.audio.dispose();
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

