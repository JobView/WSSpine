package com.ws.wsspine;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.android.AndroidApplicationBase;
import com.badlogic.gdx.backends.android.AndroidInput;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.SnapshotArray;

public abstract class WsAndroidApplicationBase implements AndroidApplicationBase {


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
    public ApplicationListener getApplicationListener() {
        return null;
    }

    @Override
    public Graphics getGraphics() {
        return null;
    }

    @Override
    public Audio getAudio() {
        return null;
    }

    @Override
    public AndroidInput getInput() {
        return null;
    }

    @Override
    public Files getFiles() {
        return null;
    }

    @Override
    public Net getNet() {
        return null;
    }

    @Override
    public void log(String s, String s1) {

    }

    @Override
    public void log(String s, String s1, Throwable throwable) {

    }

    @Override
    public void error(String s, String s1) {

    }

    @Override
    public void error(String s, String s1, Throwable throwable) {

    }

    @Override
    public void debug(String s, String s1) {

    }

    @Override
    public void debug(String s, String s1, Throwable throwable) {

    }

    @Override
    public void setLogLevel(int i) {

    }

    @Override
    public int getLogLevel() {
        return 0;
    }

    @Override
    public ApplicationType getType() {
        return null;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public long getJavaHeap() {
        return 0;
    }

    @Override
    public long getNativeHeap() {
        return 0;
    }

    @Override
    public Preferences getPreferences(String s) {
        return null;
    }

    @Override
    public Clipboard getClipboard() {
        return null;
    }

    @Override
    public void postRunnable(Runnable runnable) {

    }

    @Override
    public void exit() {

    }

    @Override
    public void addLifecycleListener(LifecycleListener lifecycleListener) {

    }

    @Override
    public void removeLifecycleListener(LifecycleListener lifecycleListener) {

    }

    @Override
    public SnapshotArray<LifecycleListener> getLifecycleListeners() {
        return new SnapshotArray<>();
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
}
