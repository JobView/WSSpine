package com.ws.wsspine.model;

import android.util.Log;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.SpineViewController;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.spine.*;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


public abstract class AbstractSpineModelLocal extends ApplicationAdapter {
    /**
     * spine系统绘制对象
     */
    OrthographicCamera camera;
    TwoColorPolygonBatch batch;
    SkeletonRenderer renderer;
    SkeletonRendererDebug debugRenderer;

    /**
     * spine 数据管理对象
     */
    TextureAtlas atlas;
    /**
     * 骨骼管理对象
     */
    Skeleton skeleton;
    SkeletonBounds bounds;
    /**
     * 动画管理对象
     */
    AnimationState animationState;

    SpineLoadListener loadListener;
    private boolean debugRendererSelect = false;

    /**
     * 图集地址
     */
    private String atlasPath;
    /**
     * json配置文件地址
     */
    private String jsonPath;
    /**
     * 控件宽
     */
    private int parentW;
    /**
     * 控件高
     */
    private int parentH;

    /**
     * 适配模式
     */
    E_SpineFitMode fitMode;

    List<String> animationsNameList = new ArrayList<>();



    public AbstractSpineModelLocal(String atlasPath, String jsonPath, int parentW, int parentH, E_SpineFitMode mode) {
        this.atlasPath = atlasPath;
        this.jsonPath = jsonPath;
        this.parentH = parentH;
        this.parentW = parentW;
        this.fitMode = mode;
    }

    public void create () {
        camera = new OrthographicCamera();
        batch = new TwoColorPolygonBatch();
        renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(true);
        debugRenderer = new SkeletonRendererDebug();

        atlas = new TextureAtlas(Gdx.files.internal(atlasPath));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        Pair<Float, Float> paintSize = FileHandleUtils.readSkeletonSize(Gdx.files.internal(jsonPath)); //只读取数据， 也可由外部传入
        json.setScale(fitMode.getScale(parentW, parentH, paintSize.getKey(), paintSize.getValue()));

        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(jsonPath));
        skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).
        float[] position = fitMode.getPosition(parentW, parentH, paintSize.getKey(), paintSize.getValue());
        skeleton.setPosition(position[0], position[1]);
        bounds = new SkeletonBounds(); // Convenience class to do hit detection with bounding boxes.
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.
        animationState = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        animationState.setTimeScale(1f); // Slow all animations down to 30% speed.
        init();

        if (loadListener != null) {
            loadListener.initFinish(getAnimations(), getSkins());
        }
    }



    protected abstract void init();
    long startTime;
    public void render () {

        if (TimeUtils.nanoTime() - startTime > 1000000000) /* 1,000,000,000ns == one second */{
            startTime = TimeUtils.nanoTime();
            Log.i("FPSLogger", "fps:" + SpineViewController.Graphics.get(this).getFramesPerSecond());
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//        animationState.update(Gdx.graphics.getDeltaTime()); // Update the animation time.

        animationState.update(SpineViewController.Graphics.get(this).getDeltaTime()); // Update the animation time.



        if (animationState.apply(skeleton)) // Poses skeleton using current animations. This sets the bones' local SRT.
            skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.


        // Configure the camera, SpriteBatch, and SkeletonRendererDebug.
//        camera.update();
        batch.getProjectionMatrix().set(camera.combined);
        debugRenderer.getShapeRenderer().setProjectionMatrix(camera.combined);

        batch.begin();
        renderer.draw(batch, skeleton); // Draw the skeleton images.
        batch.end();

        if(debugRendererSelect){
            debugRenderer.draw(skeleton); // Draw debug lines.
        }
    }

    public void resize (int width, int height) {
        camera.setToOrtho(false); // Update camera with new size.
    }

    public void dispose () {
        atlas.dispose();
    }

    public void setLoadListener(SpineLoadListener loadListener) {
        this.loadListener = loadListener;
    }

    public void setAnimation(String animationName) {
        animationState.setAnimation(0, animationName, true);
    }

    public void setSkin(String skinName) {
        skeleton.setSkin(skinName);
        skeleton.setSlotsToSetupPose();
    }

    public void setDebugRenderer(boolean debugRendererSelect) {
        this.debugRendererSelect = debugRendererSelect;
    }

    /**
     * 获取动画合集
     * @return
     */
    public List<String> getAnimations(){
        if(animationsNameList.isEmpty()){
            for (Animation animation : skeleton.getData().getAnimations()) {
                animationsNameList.add(animation.getName());
            }
        }
        return  animationsNameList;
    }

    /**
     * 获取皮肤合集
     * @return
     */
    public Array<Skin> getSkins(){
        return  skeleton.getData().getSkins();
    }

    /**
     * 获取插槽合集
     * @return
     */
    public Array<SlotData> getSlots(){
        return  skeleton.getData().getSlots();
    }

    public interface SpineLoadListener{
        void initFinish(List<String> animations, Array<Skin> skins);
    }
}
