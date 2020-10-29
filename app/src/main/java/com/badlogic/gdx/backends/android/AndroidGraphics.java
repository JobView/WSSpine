//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.badlogic.gdx.backends.android;

import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Process;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Graphics.BufferFormat;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.GraphicsType;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20API18;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18;
import com.badlogic.gdx.backends.android.surfaceview.GdxEglConfigChooser;
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;
import com.badlogic.gdx.backends.android.textureview.GLTextureView;
import com.badlogic.gdx.backends.android.textureview.GLTextureView20;
import com.badlogic.gdx.backends.android.textureview.GLTextureView.EGLConfigChooser;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureArray;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.WindowedMean;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.SnapshotArray;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

public class AndroidGraphics implements Graphics, Renderer, com.badlogic.gdx.backends.android.textureview.GLTextureView.Renderer {
	private static final String LOG_TAG = "AndroidGraphics";
	static volatile boolean enforceContinuousRendering = false;
	final View view;
	int width;
	int height;
	AndroidApplicationBase app;
	GL20 gl20;
	GL30 gl30;
	EGLContext eglContext;
	GLVersion glVersion;
	String extensions;
	protected long lastFrameTime;
	protected float deltaTime;
	protected long frameStart;
	protected long frameId;
	protected int frames;
	protected int fps;
	protected WindowedMean mean;
	volatile boolean created;
	volatile boolean running;
	volatile boolean pause;
	volatile boolean resume;
	volatile boolean destroy;
	private float ppiX;
	private float ppiY;
	private float ppcX;
	private float ppcY;
	private float density;
	protected final AndroidApplicationConfiguration config;
	private BufferFormat bufferFormat;
	private boolean isContinuous;
	int[] value;
	Object synch;

	public AndroidGraphics(AndroidApplicationBase application, AndroidApplicationConfiguration config, ResolutionStrategy resolutionStrategy) {
		this(application, config, resolutionStrategy, true);
	}

	public AndroidGraphics(AndroidApplicationBase application, AndroidApplicationConfiguration config, ResolutionStrategy resolutionStrategy, boolean focusableView) {
		this.lastFrameTime = System.nanoTime();
		this.deltaTime = 0.0F;
		this.frameStart = System.nanoTime();
		this.frameId = -1L;
		this.frames = 0;
		this.mean = new WindowedMean(5);
		this.created = false;
		this.running = false;
		this.pause = false;
		this.resume = false;
		this.destroy = false;
		this.ppiX = 0.0F;
		this.ppiY = 0.0F;
		this.ppcX = 0.0F;
		this.ppcY = 0.0F;
		this.density = 1.0F;
		this.bufferFormat = new BufferFormat(5, 6, 5, 0, 16, 0, 0, false);
		this.isContinuous = true;
		this.value = new int[1];
		this.synch = new Object();
		this.config = config;
		this.app = application;
		this.view = this.createGLSurfaceView(application, resolutionStrategy);
		this.preserveEGLContextOnPause();
		if (focusableView) {
			this.view.setFocusable(true);
			this.view.setFocusableInTouchMode(true);
		}

	}

	protected void preserveEGLContextOnPause() {
		int sdkVersion = VERSION.SDK_INT;
		if (this.view instanceof GLTextureView20 || sdkVersion >= 11 && this.view instanceof GLSurfaceView20 || this.view instanceof GLSurfaceView20API18) {
			try {
				this.view.getClass().getMethod("setPreserveEGLContextOnPause", Boolean.TYPE).invoke(this.view, true);
			} catch (Exception var3) {
				Gdx.app.log("AndroidGraphics", "Method GLSurfaceView.setPreserveEGLContextOnPause not found");
			}
		}

	}

	protected View createGLSurfaceView(AndroidApplicationBase application, ResolutionStrategy resolutionStrategy) {
		if (!this.checkGL20()) {
			throw new GdxRuntimeException("Libgdx requires OpenGL ES 2.0");
		} else if (this.config.useTextureView) {
			EGLConfigChooser configChooser = this.getTextureEglConfigChooser();
			GLTextureView view = new GLTextureView20(application.getContext(), resolutionStrategy, this.config.useGL30 ? 3 : 2);
			if (configChooser != null) {
				view.setEGLConfigChooser(configChooser);
			} else {
				view.setEGLConfigChooser(this.config.r, this.config.g, this.config.b, this.config.a, this.config.depth, this.config.stencil);
			}

			view.setOpaque(false);
			view.setRenderer(this);
			return view;
		} else {
			android.opengl.GLSurfaceView.EGLConfigChooser configChooser = this.getEglConfigChooser();
			int sdkVersion = VERSION.SDK_INT;
			if (sdkVersion <= 10 && this.config.useGLSurfaceView20API18) {
				GLSurfaceView20API18 view = new GLSurfaceView20API18(application.getContext(), resolutionStrategy);
				if (configChooser != null) {
					view.setEGLConfigChooser(configChooser);
				} else {
					view.setEGLConfigChooser(this.config.r, this.config.g, this.config.b, this.config.a, this.config.depth, this.config.stencil);
				}

				view.setRenderer(this);
				return view;
			} else {
				GLSurfaceView20 view = new GLSurfaceView20(application.getContext(), resolutionStrategy, this.config.useGL30 ? 3 : 2);
				if (configChooser != null) {
					view.setEGLConfigChooser(configChooser);
				} else {
					view.setEGLConfigChooser(this.config.r, this.config.g, this.config.b, this.config.a, this.config.depth, this.config.stencil);
				}

				view.setRenderer(this);
				return view;
			}
		}
	}

	public void onPauseGLSurfaceView() {
		if (this.view != null) {
			if (this.view instanceof GLTextureView20) {
				((GLTextureView20)this.view).onPause();
			} else if (this.view instanceof GLSurfaceViewAPI18) {
				((GLSurfaceViewAPI18)this.view).onPause();
			} else if (this.view instanceof GLSurfaceView) {
				((GLSurfaceView)this.view).onPause();
			}
		}

	}

	public void onResumeGLSurfaceView() {
		if (this.view != null) {
			if (this.view instanceof GLTextureView20) {
				((GLTextureView20)this.view).onResume();
			} else if (this.view instanceof GLSurfaceViewAPI18) {
				((GLSurfaceViewAPI18)this.view).onResume();
			} else if (this.view instanceof GLSurfaceView) {
				((GLSurfaceView)this.view).onResume();
			}
		}

	}

	protected android.opengl.GLSurfaceView.EGLConfigChooser getEglConfigChooser() {
		return new GdxEglConfigChooser(this.config.r, this.config.g, this.config.b, this.config.a, this.config.depth, this.config.stencil, this.config.numSamples);
	}

	protected EGLConfigChooser getTextureEglConfigChooser() {
		return new com.badlogic.gdx.backends.android.textureview.GdxEglConfigChooser(this.config.r, this.config.g, this.config.b, this.config.a, this.config.depth, this.config.stencil, this.config.numSamples);
	}

	protected void updatePpi() {
		DisplayMetrics metrics = new DisplayMetrics();
		this.app.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		this.ppiX = metrics.xdpi;
		this.ppiY = metrics.ydpi;
		this.ppcX = metrics.xdpi / 2.54F;
		this.ppcY = metrics.ydpi / 2.54F;
		this.density = metrics.density;
	}

	protected boolean checkGL20() {
		EGL10 egl = (EGL10)EGLContext.getEGL();
		EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
		int[] version = new int[2];
		egl.eglInitialize(display, version);
		int EGL_OPENGL_ES2_BIT = 4;
		int[] configAttribs = new int[]{12324, 4, 12323, 4, 12322, 4, 12352, EGL_OPENGL_ES2_BIT, 12344};
		EGLConfig[] configs = new EGLConfig[10];
		int[] num_config = new int[1];
		egl.eglChooseConfig(display, configAttribs, configs, 10, num_config);
		egl.eglTerminate(display);
		return num_config[0] > 0;
	}

	public GL20 getGL20() {
		return this.gl20;
	}

	public void setGL20(GL20 gl20) {
		this.gl20 = gl20;
		if (this.gl30 == null) {
			Gdx.gl = gl20;
			Gdx.gl20 = gl20;
		}

	}

	public boolean isGL30Available() {
		return this.gl30 != null;
	}

	public GL30 getGL30() {
		return this.gl30;
	}

	public void setGL30(GL30 gl30) {
		this.gl30 = gl30;
		if (gl30 != null) {
			this.gl20 = gl30;
			Gdx.gl = this.gl20;
			Gdx.gl20 = this.gl20;
			Gdx.gl30 = gl30;
		}

	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public int getBackBufferWidth() {
		return this.width;
	}

	public int getBackBufferHeight() {
		return this.height;
	}

	protected void setupGL(GL10 gl) {
		String versionString = gl.glGetString(7938);
		String vendorString = gl.glGetString(7936);
		String rendererString = gl.glGetString(7937);
		this.glVersion = new GLVersion(ApplicationType.Android, versionString, vendorString, rendererString);
		if (this.config.useGL30 && this.glVersion.getMajorVersion() > 2) {
			if (this.gl30 != null) {
				return;
			}

			this.gl20 = this.gl30 = new AndroidGL30();
			Gdx.gl = this.gl30;
			Gdx.gl20 = this.gl30;
			Gdx.gl30 = this.gl30;
		} else {
			if (this.gl20 != null) {
				return;
			}

			this.gl20 = new AndroidGL20();
			Gdx.gl = this.gl20;
			Gdx.gl20 = this.gl20;
		}

		Gdx.app.log("AndroidGraphics", "OGL renderer: " + gl.glGetString(7937));
		Gdx.app.log("AndroidGraphics", "OGL vendor: " + gl.glGetString(7936));
		Gdx.app.log("AndroidGraphics", "OGL version: " + gl.glGetString(7938));
		Gdx.app.log("AndroidGraphics", "OGL extensions: " + gl.glGetString(7939));
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		this.width = width;
		this.height = height;
		this.updatePpi();
		gl.glViewport(0, 0, this.width, this.height);
		if (!this.created) {
			this.app.getApplicationListener().create();
			this.created = true;
			synchronized(this) {
				this.running = true;
			}
		}

		this.app.getApplicationListener().resize(width, height);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		this.eglContext = ((EGL10)EGLContext.getEGL()).eglGetCurrentContext();
		this.setupGL(gl);
		this.logConfig(config);
		this.updatePpi();
		Mesh.invalidateAllMeshes(this.app);
		Texture.invalidateAllTextures(this.app);
		Cubemap.invalidateAllCubemaps(this.app);
		TextureArray.invalidateAllTextureArrays(this.app);
		ShaderProgram.invalidateAllShaderPrograms(this.app);
		FrameBuffer.invalidateAllFrameBuffers(this.app);
		this.logManagedCachesStatus();
		Display display = this.app.getWindowManager().getDefaultDisplay();
		this.width = display.getWidth();
		this.height = display.getHeight();
		this.mean = new WindowedMean(5);
		this.lastFrameTime = System.nanoTime();
		gl.glViewport(0, 0, this.width, this.height);
	}

	protected void logConfig(EGLConfig config) {
		EGL10 egl = (EGL10)EGLContext.getEGL();
		EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
		int r = this.getAttrib(egl, display, config, 12324, 0);
		int g = this.getAttrib(egl, display, config, 12323, 0);
		int b = this.getAttrib(egl, display, config, 12322, 0);
		int a = this.getAttrib(egl, display, config, 12321, 0);
		int d = this.getAttrib(egl, display, config, 12325, 0);
		int s = this.getAttrib(egl, display, config, 12326, 0);
		int samples = Math.max(this.getAttrib(egl, display, config, 12337, 0), this.getAttrib(egl, display, config, 12513, 0));
		boolean coverageSample = this.getAttrib(egl, display, config, 12513, 0) != 0;
		Gdx.app.log("AndroidGraphics", "framebuffer: (" + r + ", " + g + ", " + b + ", " + a + ")");
		Gdx.app.log("AndroidGraphics", "depthbuffer: (" + d + ")");
		Gdx.app.log("AndroidGraphics", "stencilbuffer: (" + s + ")");
		Gdx.app.log("AndroidGraphics", "samples: (" + samples + ")");
		Gdx.app.log("AndroidGraphics", "coverage sampling: (" + coverageSample + ")");
		this.bufferFormat = new BufferFormat(r, g, b, a, d, s, samples, coverageSample);
	}

	private int getAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attrib, int defValue) {
		return egl.eglGetConfigAttrib(display, config, attrib, this.value) ? this.value[0] : defValue;
	}

	void resume() {
		synchronized(this.synch) {
			this.running = true;
			this.resume = true;
		}
	}

	void pause() {
		synchronized(this.synch) {
			if (this.running) {
				this.running = false;
				this.pause = true;

				while(this.pause) {
					try {
						this.synch.wait(4000L);
						if (this.pause) {
							Gdx.app.error("AndroidGraphics", "waiting for pause synchronization took too long; assuming deadlock and killing");
							Process.killProcess(Process.myPid());
						}
					} catch (InterruptedException var4) {
						Gdx.app.log("AndroidGraphics", "waiting for pause synchronization failed!");
					}
				}

			}
		}
	}

	void destroy() {
		synchronized(this.synch) {
			this.running = false;
			this.destroy = true;

			while(this.destroy) {
				try {
					this.synch.wait();
				} catch (InterruptedException var4) {
					Gdx.app.log("AndroidGraphics", "waiting for destroy synchronization failed!");
				}
			}

		}
	}

	public void onDrawFrame(GL10 gl) {
		long time = System.nanoTime();
		this.deltaTime = (float)(time - this.lastFrameTime) / 1.0E9F;
		this.lastFrameTime = time;
		if (!this.resume) {
			this.mean.addValue(this.deltaTime);
		} else {
			this.deltaTime = 0.0F;
		}

		boolean lrunning = false;
		boolean lpause = false;
		boolean ldestroy = false;
		boolean lresume = false;
		synchronized(this.synch) {
			lrunning = this.running;
			lpause = this.pause;
			ldestroy = this.destroy;
			lresume = this.resume;
			if (this.resume) {
				this.resume = false;
			}

			if (this.pause) {
				this.pause = false;
				this.synch.notifyAll();
			}

			if (this.destroy) {
				this.destroy = false;
				this.synch.notifyAll();
			}
		}

		SnapshotArray lifecycleListeners;
		LifecycleListener[] listeners;
		int i;
		int n;
		if (lresume) {
			lifecycleListeners = this.app.getLifecycleListeners();
			synchronized(lifecycleListeners) {
				listeners = (LifecycleListener[])lifecycleListeners.begin();
				i = 0;
				n = lifecycleListeners.size;

				while(true) {
					if (i >= n) {
						lifecycleListeners.end();
						break;
					}

					listeners[i].resume();
					++i;
				}
			}

			this.app.getApplicationListener().resume();
			Gdx.app.log("AndroidGraphics", "resumed");
		}

		if (lrunning) {
			synchronized(this.app.getRunnables()) {
				this.app.getExecutedRunnables().clear();
				this.app.getExecutedRunnables().addAll(this.app.getRunnables());
				this.app.getRunnables().clear();
			}

			for(i = 0; i < this.app.getExecutedRunnables().size; ++i) {
				try {
					((Runnable)this.app.getExecutedRunnables().get(i)).run();
				} catch (Throwable var17) {
					var17.printStackTrace();
				}
			}

			this.app.getInput().processEvents();
			++this.frameId;
			this.app.getApplicationListener().render();
		}

		if (lpause) {
			lifecycleListeners = this.app.getLifecycleListeners();
			synchronized(lifecycleListeners) {
				listeners = (LifecycleListener[])lifecycleListeners.begin();
				i = 0;
				n = lifecycleListeners.size;

				while(true) {
					if (i >= n) {
						break;
					}

					listeners[i].pause();
					++i;
				}
			}

			this.app.getApplicationListener().pause();
			Gdx.app.log("AndroidGraphics", "paused");
		}

		if (ldestroy) {
			lifecycleListeners = this.app.getLifecycleListeners();
			synchronized(lifecycleListeners) {
				listeners = (LifecycleListener[])lifecycleListeners.begin();
				i = 0;
				n = lifecycleListeners.size;

				while(true) {
					if (i >= n) {
						break;
					}

					listeners[i].dispose();
					++i;
				}
			}

			this.app.getApplicationListener().dispose();
			Gdx.app.log("AndroidGraphics", "destroyed");
		}

		if (time - this.frameStart > 1000000000L) {
			this.fps = this.frames;
			this.frames = 0;
			this.frameStart = time;
		}

		++this.frames;
	}

	public long getFrameId() {
		return this.frameId;
	}

	public float getDeltaTime() {
		return this.mean.getMean() == 0.0F ? this.deltaTime : this.mean.getMean();
	}

	public float getRawDeltaTime() {
		return this.deltaTime;
	}

	public GraphicsType getType() {
		return GraphicsType.AndroidGL;
	}

	public GLVersion getGLVersion() {
		return this.glVersion;
	}

	public int getFramesPerSecond() {
		return this.fps;
	}

	public void clearManagedCaches() {
		Mesh.clearAllMeshes(this.app);
		Texture.clearAllTextures(this.app);
		Cubemap.clearAllCubemaps(this.app);
		TextureArray.clearAllTextureArrays(this.app);
		ShaderProgram.clearAllShaderPrograms(this.app);
		FrameBuffer.clearAllFrameBuffers(this.app);
		this.logManagedCachesStatus();
	}

	protected void logManagedCachesStatus() {
		Gdx.app.log("AndroidGraphics", Mesh.getManagedStatus());
		Gdx.app.log("AndroidGraphics", Texture.getManagedStatus());
		Gdx.app.log("AndroidGraphics", Cubemap.getManagedStatus());
		Gdx.app.log("AndroidGraphics", ShaderProgram.getManagedStatus());
		Gdx.app.log("AndroidGraphics", FrameBuffer.getManagedStatus());
	}

	public View getView() {
		return this.view;
	}

	public float getPpiX() {
		return this.ppiX;
	}

	public float getPpiY() {
		return this.ppiY;
	}

	public float getPpcX() {
		return this.ppcX;
	}

	public float getPpcY() {
		return this.ppcY;
	}

	public float getDensity() {
		return this.density;
	}

	public boolean supportsDisplayModeChange() {
		return false;
	}

	public boolean setFullscreenMode(DisplayMode displayMode) {
		return false;
	}

	public Monitor getPrimaryMonitor() {
		return new AndroidGraphics.AndroidMonitor(0, 0, "Primary Monitor");
	}

	public Monitor getMonitor() {
		return this.getPrimaryMonitor();
	}

	public Monitor[] getMonitors() {
		return new Monitor[]{this.getPrimaryMonitor()};
	}

	public DisplayMode[] getDisplayModes(Monitor monitor) {
		return this.getDisplayModes();
	}

	public DisplayMode getDisplayMode(Monitor monitor) {
		return this.getDisplayMode();
	}

	public DisplayMode[] getDisplayModes() {
		return new DisplayMode[]{this.getDisplayMode()};
	}

	public boolean setWindowedMode(int width, int height) {
		return false;
	}

	public void setTitle(String title) {
	}

	public void setUndecorated(boolean undecorated) {
		int mask = undecorated ? 1 : 0;
		this.app.getApplicationWindow().setFlags(1024, mask);
	}

	public void setResizable(boolean resizable) {
	}

	public DisplayMode getDisplayMode() {
		DisplayMetrics metrics = new DisplayMetrics();
		this.app.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return new AndroidGraphics.AndroidDisplayMode(metrics.widthPixels, metrics.heightPixels, 0, 0);
	}

	public BufferFormat getBufferFormat() {
		return this.bufferFormat;
	}

	public void setVSync(boolean vsync) {
	}

	public boolean supportsExtension(String extension) {
		if (this.extensions == null) {
			this.extensions = Gdx.gl.glGetString(7939);
		}

		return this.extensions.contains(extension);
	}

	public void setContinuousRendering(boolean isContinuous) {
		if (this.view != null) {
			this.isContinuous = enforceContinuousRendering || isContinuous;
			int renderMode = this.isContinuous ? 1 : 0;
			if (this.view instanceof GLTextureView20) {
				((GLTextureView20)this.view).setRenderMode(renderMode);
			} else if (this.view instanceof GLSurfaceViewAPI18) {
				((GLSurfaceViewAPI18)this.view).setRenderMode(renderMode);
			} else if (this.view instanceof GLSurfaceView) {
				((GLSurfaceView)this.view).setRenderMode(renderMode);
			}

			this.mean.clear();
		}

	}

	public boolean isContinuousRendering() {
		return this.isContinuous;
	}

	public void requestRendering() {
		if (this.view != null) {
			if (this.view instanceof GLTextureView20) {
				((GLTextureView20)this.view).requestRender();
			} else if (this.view instanceof GLSurfaceViewAPI18) {
				((GLSurfaceViewAPI18)this.view).requestRender();
			} else if (this.view instanceof GLSurfaceView) {
				((GLSurfaceView)this.view).requestRender();
			}
		}

	}

	public boolean isFullscreen() {
		return true;
	}

	public Cursor newCursor(Pixmap pixmap, int xHotspot, int yHotspot) {
		return null;
	}

	public void setCursor(Cursor cursor) {
	}

	public void setSystemCursor(SystemCursor systemCursor) {
	}

	private class AndroidMonitor extends Monitor {
		public AndroidMonitor(int virtualX, int virtualY, String name) {
			super(virtualX, virtualY, name);
		}
	}

	private class AndroidDisplayMode extends DisplayMode {
		protected AndroidDisplayMode(int width, int height, int refreshRate, int bitsPerPixel) {
			super(width, height, refreshRate, bitsPerPixel);
		}
	}
}
