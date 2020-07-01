package com.badlogic.gdx.backends.android;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.badlogic.gdx.utils.SnapshotArray;

public class SpineManager extends AndroidApplication{
    protected Context mContext;
    protected AndroidGraphics graphics;
    protected ApplicationListener listener;
    static {
        GdxNativesLoader.load();
    }
    
    private static class InnerClass {
        private static SpineManager singletonInstance = new SpineManager();
    }
    
    public SpineManager(){}
    
    public static SpineManager getInstance(){
            return InnerClass.singletonInstance;
    }


    public static void main(String[] args) {
//
    }
    

    public View initializeForView(ApplicationListener listener, AndroidApplicationConfiguration config, Context context) {
        this.init(listener, config);
        this.listener = listener;
        this.mContext = context;
        return this.graphics.getView();
    }

    private void init(ApplicationListener listener, AndroidApplicationConfiguration config) {
        this.graphics = new AndroidGraphics(this, config, (ResolutionStrategy) (config.resolutionStrategy == null ? new FillResolutionStrategy() : config.resolutionStrategy));
        Gdx.app = this;
        Gdx.graphics = graphics;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public Array<Runnable> getRunnables() {
        return null;
    }

    @Override
    public Array<Runnable> getExecutedRunnables() {
        return null;
    }

    @Override
    public void runOnUiThread(Runnable runnable) {

    }


    @Override
    public void startActivity(Intent intent) {

    }

    @Override
    public AndroidInput getInput() {
        return null;
    }

    @Override
    public SnapshotArray<LifecycleListener> getLifecycleListeners() {
        return null;
    }

    @Override
    public Window getApplicationWindow() {
        return null;
    }

    @Override
    public WindowManager getWindowManager() {
        return null;
    }

    @Override
    public void useImmersiveMode(boolean b) {

    }

    @Override
    public Handler getHandler() {
        return null;
    }


    @Override
    public ApplicationListener getApplicationListener() {
        return this.listener;
    }
}
