package com.badlogic.gdx.backends.android;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.View;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.lang.reflect.Method;

public class SpineViewController extends WSSpineBaseApplication {
//    public static Map<SpineViewController, AndroidGraphics> Graphics = new HashMap();


    public SpineViewController(Context context){
        super(context);
    }


    public View initializeForView(ApplicationListener listener, AndroidApplicationConfiguration config) {
        this.init(listener, config);
        return this.graphics.getView();
    }


    private void init (ApplicationListener listener, AndroidApplicationConfiguration config) {
        if (this.getVersion() < MINIMUM_SDK) {
            throw new GdxRuntimeException("LibGDX requires Android API Level " + MINIMUM_SDK + " or later.");
        }
        setApplicationLogger(new AndroidApplicationLogger());
        graphics = new AndroidGraphics(this, config, config.resolutionStrategy == null ? new FillResolutionStrategy()
                : config.resolutionStrategy);
        input = AndroidInputFactory.newAndroidInput(this, mContext, graphics.view, config);
        audio = new AndroidAudio(mContext, config);
        this.mContext.getFilesDir(); // workaround for Android bug #10515463
        files = new AndroidFiles(mContext.getAssets(), mContext.getFilesDir().getAbsolutePath());
        net = new AndroidNet(this, config);
        this.listener = listener;
        this.handler = new Handler();
        this.useImmersiveMode = config.useImmersiveMode;
        this.hideStatusBar = config.hideStatusBar;
        this.clipboard = new AndroidClipboard(mContext);

        // Add a specialized audio lifecycle listener
        addLifecycleListener(new LifecycleListener() {

            @Override
            public void resume () {
                // No need to resume audio here
            }

            @Override
            public void pause () {
                audio.pause();
            }

            @Override
            public void dispose () {
                audio.dispose();
            }
        });

        Gdx.app = this;
        Gdx.input = this.getInput();
        Gdx.audio = this.getAudio();
        Gdx.files = this.getFiles();
        Gdx.graphics = this.getGraphics();
        Gdx.net = this.getNet();
        useImmersiveMode(this.useImmersiveMode);
        if (this.useImmersiveMode && getVersion() >= Build.VERSION_CODES.KITKAT) {
            try {
                Class<?> vlistener = Class.forName("com.badlogic.gdx.backends.android.AndroidVisibilityListener");
                Object o = vlistener.newInstance();
                Method method = vlistener.getDeclaredMethod("createListener", AndroidApplicationBase.class);
                method.invoke(o, this);
            } catch (Exception e) {
                log("AndroidApplication", "Failed to create AndroidVisibilityListener", e);
            }
        }

    }

    public void release() {
        onDestroy();
    }
}
