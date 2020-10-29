//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.badlogic.gdx.backends.android;

import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;

public class AndroidApplicationConfiguration {
	public int r = 5;
	public int g = 6;
	public int b = 5;
	public int a = 0;
	public int depth = 16;
	public int stencil = 0;
	public int numSamples = 0;
	public boolean useAccelerometer = true;
	public boolean useGyroscope = false;
	public boolean useCompass = true;
	public boolean useRotationVectorSensor = false;
	public int sensorDelay = 1;
	public int touchSleepTime = 0;
	public boolean useWakelock = false;
	public boolean hideStatusBar = false;
	public boolean disableAudio = false;
	public int maxSimultaneousSounds = 16;
	public ResolutionStrategy resolutionStrategy = new FillResolutionStrategy();
	public boolean getTouchEventsForLiveWallpaper = false;
	public boolean useImmersiveMode = false;
	/** @deprecated */
	@Deprecated
	public boolean useGL30 = false;
	public boolean useGLSurfaceView20API18 = false;
	public int maxNetThreads = 2147483647;
	public boolean useTextureView = false;

	public AndroidApplicationConfiguration() {
	}
}
