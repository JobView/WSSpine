package com.badlogic.gdx.backends.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.SnapshotArray;

import java.lang.reflect.Method;

public abstract class WSSpineBaseApplication implements AndroidApplicationBase {
    static {
        GdxNativesLoader.load();
    }
    private WindowManager mWindowManager;
    public WSSpineBaseApplication(Context context) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    protected Context mContext;
    protected AndroidGraphics graphics;
    protected AndroidInput input;
    protected AndroidAudio audio;
    protected AndroidFiles files;
    protected AndroidNet net;
    protected AndroidClipboard clipboard;
    protected ApplicationListener listener;
    public Handler handler;
    protected boolean firstResume = true;
    protected final Array<Runnable> runnables = new Array<Runnable>();
    protected final Array<Runnable> executedRunnables = new Array<Runnable>();
    protected final SnapshotArray<LifecycleListener> lifecycleListeners = new SnapshotArray<LifecycleListener>(LifecycleListener.class);
    private final Array<AndroidEventListener> androidEventListeners = new Array<AndroidEventListener>();
    protected int logLevel = LOG_INFO;
    protected ApplicationLogger applicationLogger;
    protected boolean useImmersiveMode = false;
    protected boolean hideStatusBar = false;
    private int wasFocusChanged = -1;
    private boolean isWaitingForAudio = false;

    protected void onPause () {
        Gdx.app.error("AndroidGraphics", "protected void onPause ()");
        boolean isContinuous = graphics.isContinuousRendering();
        boolean isContinuousEnforced = AndroidGraphics.enforceContinuousRendering;

        // from here we don't want non continuous rendering
        AndroidGraphics.enforceContinuousRendering = true;
        graphics.setContinuousRendering(true);
        // calls to setContinuousRendering(false) from other thread (ex: GLThread)
        // will be ignored at this point...
        graphics.pause();


//        if (isFinishing()) {
            graphics.clearManagedCaches();
            graphics.destroy();
//        }

        AndroidGraphics.enforceContinuousRendering = isContinuousEnforced;
        graphics.setContinuousRendering(isContinuous);

        graphics.onPauseGLSurfaceView();

    }

    protected void onResume () {
        Gdx.app = this;
        Gdx.input = this.getInput();
        Gdx.audio = this.getAudio();
        Gdx.files = this.getFiles();
        Gdx.graphics = this.getGraphics();
        Gdx.net = this.getNet();


        if (graphics != null) {
            graphics.onResumeGLSurfaceView();
        }

        if (!firstResume) {
            graphics.resume();
        } else
            firstResume = false;

        this.isWaitingForAudio = true;
        if (this.wasFocusChanged == 1 || this.wasFocusChanged == -1) {
            this.audio.resume();
            this.isWaitingForAudio = false;
        }
    }

    protected void onDestroy () {
        onPause();
    }

    @Override
    public ApplicationListener getApplicationListener () {
        return listener;
    }

    @Override
    public Audio getAudio () {
        return audio;
    }

    @Override
    public Files getFiles () {
        return files;
    }

    @Override
    public Graphics getGraphics () {
        return graphics;
    }

    @Override
    public AndroidInput getInput () {
        return input;
    }

    @Override
    public Net getNet () {
        return net;
    }

    @Override
    public ApplicationType getType () {
        return ApplicationType.Android;
    }

    @Override
    public int getVersion () {
        return Build.VERSION.SDK_INT;
    }

    @Override
    public long getJavaHeap () {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    @Override
    public long getNativeHeap () {
        return Debug.getNativeHeapAllocatedSize();
    }

    @Override
    public Preferences getPreferences (String name) {
        return new AndroidPreferences(mContext.getSharedPreferences(name, Context.MODE_PRIVATE));
    }


    @Override
    public Clipboard getClipboard () {
        return clipboard;
    }

    @Override
    public void postRunnable (Runnable runnable) {
        synchronized (runnables) {
            runnables.add(runnable);
            Gdx.graphics.requestRendering();
        }
    }


    @Override
    public void exit () {
        handler.post(new Runnable() {
            @Override
            public void run () {

            }
        });
    }

    @Override
    public void debug (String tag, String message) {
        if (logLevel >= LOG_DEBUG) getApplicationLogger().debug(tag, message);
    }

    @Override
    public void debug (String tag, String message, Throwable exception) {
        if (logLevel >= LOG_DEBUG) getApplicationLogger().debug(tag, message, exception);
    }

    @Override
    public void log (String tag, String message) {
        if (logLevel >= LOG_INFO) getApplicationLogger().log(tag, message);
    }

    @Override
    public void log (String tag, String message, Throwable exception) {
        if (logLevel >= LOG_INFO) getApplicationLogger().log(tag, message, exception);
    }

    @Override
    public void error (String tag, String message) {
        if (logLevel >= LOG_ERROR) getApplicationLogger().error(tag, message);
    }

    @Override
    public void error (String tag, String message, Throwable exception) {
        if (logLevel >= LOG_ERROR) getApplicationLogger().error(tag, message, exception);
    }

    @Override
    public void setLogLevel (int logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public int getLogLevel () {
        return logLevel;
    }

    @Override
    public void setApplicationLogger (ApplicationLogger applicationLogger) {
        this.applicationLogger = applicationLogger;
    }

    @Override
    public ApplicationLogger getApplicationLogger () {
        return applicationLogger;
    }

    @Override
    public void addLifecycleListener (LifecycleListener listener) {
        synchronized (lifecycleListeners) {
            lifecycleListeners.add(listener);
        }
    }

    @Override
    public void removeLifecycleListener (LifecycleListener listener) {
        synchronized (lifecycleListeners) {
            lifecycleListeners.removeValue(listener, true);
        }
    }


    /** Adds an event listener for Android specific event such as onActivityResult(...). */
    public void addAndroidEventListener (AndroidEventListener listener) {
        synchronized (androidEventListeners) {
            androidEventListeners.add(listener);
        }
    }

    /** Removes an event listener for Android specific event such as onActivityResult(...). */
    public void removeAndroidEventListener (AndroidEventListener listener) {
        synchronized (androidEventListeners) {
            androidEventListeners.removeValue(listener, true);
        }
    }

    @Override
    public Context getContext () {
        return mContext;
    }

    @Override
    public Array<Runnable> getRunnables () {
        return runnables;
    }

    @Override
    public Array<Runnable> getExecutedRunnables () {
        return executedRunnables;
    }

    @Override
    public SnapshotArray<LifecycleListener> getLifecycleListeners () {
        return lifecycleListeners;
    }

    public WindowManager getWindowManager() {
        return mWindowManager;
    }

    @Override
    public Window getApplicationWindow () {
        if(mContext instanceof Activity){
            return ((Activity) mContext).getWindow();
        }
        return null;
    }

    @Override
    public Handler getHandler () {
        return this.handler;
    }


    @Override
    public void runOnUiThread(Runnable runnable) {

    }

    @Override
    public void startActivity(Intent intent) {

    }

    @Override
    public void useImmersiveMode(boolean b) {

    }

}
