//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.badlogic.gdx.backends.android.textureview;

import android.util.Log;
import com.badlogic.gdx.backends.android.textureview.GLTextureView.EGLConfigChooser;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

public class GdxEglConfigChooser implements EGLConfigChooser {
    private static final int EGL_OPENGL_ES2_BIT = 4;
    public static final int EGL_COVERAGE_BUFFERS_NV = 12512;
    public static final int EGL_COVERAGE_SAMPLES_NV = 12513;
    private static final String TAG = "GdxEglConfigChooser";
    protected int mRedSize;
    protected int mGreenSize;
    protected int mBlueSize;
    protected int mAlphaSize;
    protected int mDepthSize;
    protected int mStencilSize;
    protected int mNumSamples;
    protected final int[] mConfigAttribs;
    private int[] mValue = new int[1];

    public GdxEglConfigChooser(int r, int g, int b, int a, int depth, int stencil, int numSamples) {
        this.mRedSize = r;
        this.mGreenSize = g;
        this.mBlueSize = b;
        this.mAlphaSize = a;
        this.mDepthSize = depth;
        this.mStencilSize = stencil;
        this.mNumSamples = numSamples;
        this.mConfigAttribs = new int[]{12324, 4, 12323, 4, 12322, 4, 12352, 4, 12344};
    }

    public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
        int[] num_config = new int[1];
        egl.eglChooseConfig(display, this.mConfigAttribs, (EGLConfig[])null, 0, num_config);
        int numConfigs = num_config[0];
        if (numConfigs <= 0) {
            throw new IllegalArgumentException("No configs match configSpec");
        } else {
            EGLConfig[] configs = new EGLConfig[numConfigs];
            egl.eglChooseConfig(display, this.mConfigAttribs, configs, numConfigs, num_config);
            EGLConfig config = this.chooseConfig(egl, display, configs);
            return config;
        }
    }

    public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
        EGLConfig best = null;
        EGLConfig bestAA = null;
        EGLConfig safe = null;
        EGLConfig[] var7 = configs;
        int var8 = configs.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            EGLConfig config = var7[var9];
            int d = this.findConfigAttrib(egl, display, config, 12325, 0);
            int s = this.findConfigAttrib(egl, display, config, 12326, 0);
            if (d >= this.mDepthSize && s >= this.mStencilSize) {
                int r = this.findConfigAttrib(egl, display, config, 12324, 0);
                int g = this.findConfigAttrib(egl, display, config, 12323, 0);
                int b = this.findConfigAttrib(egl, display, config, 12322, 0);
                int a = this.findConfigAttrib(egl, display, config, 12321, 0);
                if (safe == null && r == 5 && g == 6 && b == 5 && a == 0) {
                    safe = config;
                }

                if (best == null && r == this.mRedSize && g == this.mGreenSize && b == this.mBlueSize && a == this.mAlphaSize) {
                    best = config;
                    if (this.mNumSamples == 0) {
                        break;
                    }
                }

                int hasSampleBuffers = this.findConfigAttrib(egl, display, config, 12338, 0);
                int numSamples = this.findConfigAttrib(egl, display, config, 12337, 0);
                if (bestAA == null && hasSampleBuffers == 1 && numSamples >= this.mNumSamples && r == this.mRedSize && g == this.mGreenSize && b == this.mBlueSize && a == this.mAlphaSize) {
                    bestAA = config;
                } else {
                    hasSampleBuffers = this.findConfigAttrib(egl, display, config, 12512, 0);
                    numSamples = this.findConfigAttrib(egl, display, config, 12513, 0);
                    if (bestAA == null && hasSampleBuffers == 1 && numSamples >= this.mNumSamples && r == this.mRedSize && g == this.mGreenSize && b == this.mBlueSize && a == this.mAlphaSize) {
                        bestAA = config;
                    }
                }
            }
        }

        if (bestAA != null) {
            return bestAA;
        } else {
            return best != null ? best : safe;
        }
    }

    private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue) {
        return egl.eglGetConfigAttrib(display, config, attribute, this.mValue) ? this.mValue[0] : defaultValue;
    }

    private void printConfigs(EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
        int numConfigs = configs.length;
        Log.w("GdxEglConfigChooser", String.format("%d configurations", numConfigs));

        for(int i = 0; i < numConfigs; ++i) {
            Log.w("GdxEglConfigChooser", String.format("Configuration %d:\n", i));
            this.printConfig(egl, display, configs[i]);
        }

    }

    private void printConfig(EGL10 egl, EGLDisplay display, EGLConfig config) {
        int[] attributes = new int[]{12320, 12321, 12322, 12323, 12324, 12325, 12326, 12327, 12328, 12329, 12330, 12331, 12332, 12333, 12334, 12335, 12336, 12337, 12338, 12339, 12340, 12343, 12342, 12341, 12345, 12346, 12347, 12348, 12349, 12350, 12351, 12352, 12354, 12512, 12513};
        String[] names = new String[]{"EGL_BUFFER_SIZE", "EGL_ALPHA_SIZE", "EGL_BLUE_SIZE", "EGL_GREEN_SIZE", "EGL_RED_SIZE", "EGL_DEPTH_SIZE", "EGL_STENCIL_SIZE", "EGL_CONFIG_CAVEAT", "EGL_CONFIG_ID", "EGL_LEVEL", "EGL_MAX_PBUFFER_HEIGHT", "EGL_MAX_PBUFFER_PIXELS", "EGL_MAX_PBUFFER_WIDTH", "EGL_NATIVE_RENDERABLE", "EGL_NATIVE_VISUAL_ID", "EGL_NATIVE_VISUAL_TYPE", "EGL_PRESERVED_RESOURCES", "EGL_SAMPLES", "EGL_SAMPLE_BUFFERS", "EGL_SURFACE_TYPE", "EGL_TRANSPARENT_TYPE", "EGL_TRANSPARENT_RED_VALUE", "EGL_TRANSPARENT_GREEN_VALUE", "EGL_TRANSPARENT_BLUE_VALUE", "EGL_BIND_TO_TEXTURE_RGB", "EGL_BIND_TO_TEXTURE_RGBA", "EGL_MIN_SWAP_INTERVAL", "EGL_MAX_SWAP_INTERVAL", "EGL_LUMINANCE_SIZE", "EGL_ALPHA_MASK_SIZE", "EGL_COLOR_BUFFER_TYPE", "EGL_RENDERABLE_TYPE", "EGL_CONFORMANT", "EGL_COVERAGE_BUFFERS_NV", "EGL_COVERAGE_SAMPLES_NV"};
        int[] value = new int[1];

        for(int i = 0; i < attributes.length; ++i) {
            int attribute = attributes[i];
            String name = names[i];
            if (egl.eglGetConfigAttrib(display, config, attribute, value)) {
                Log.w("GdxEglConfigChooser", String.format("  %s: %d\n", name, value[0]));
            } else {
                egl.eglGetError();
            }
        }

    }
}
