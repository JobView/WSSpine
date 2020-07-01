package com.ws.wsspine.model;

import android.text.TextUtils;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonBounds;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.SkeletonRendererDebug;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;

public class Mumu extends ApplicationAdapter {

    OrthographicCamera camera;
    PolygonSpriteBatch batch;
    SkeletonRenderer renderer;
    SkeletonRendererDebug debugRenderer;

    SkeletonBounds bounds;
    TextureAtlas atlas;
    Skeleton skeleton;
    AnimationState state;
    SkeletonJson json;

    boolean isClickAnimation = false;

    public void create() {
        camera = new OrthographicCamera();
        batch = new PolygonSpriteBatch();
        renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(true); // PMA results in correct blending without outlines.
        debugRenderer = new SkeletonRendererDebug();
        debugRenderer.setMeshTriangles(false);
//        debugRenderer.setBoundingBoxes(false);
        debugRenderer.setRegionAttachments(false);
        debugRenderer.setMeshHull(false);

        atlas = new TextureAtlas(Gdx.files.internal("mumu/mumuzhayan.atlas"));
        json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(0.3f); // Load the skeleton at 60% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("mumu/mumuzhayan.json"));

        skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).
        skeleton.setPosition(300, 20);

        bounds = new SkeletonBounds(); // Convenience class to do hit detection with bounding boxes.

        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        state.setTimeScale(1.0f); // Slow all animations down to 50% speed.

        // Queue animations on track 0.
        state.setAnimation(0, "daiji", true);

        addListener();
    }

    private void addListener() {
        state.addListener(new AnimationState.AnimationStateListener() {
            @Override
            public void start(AnimationState.TrackEntry entry) {
                if(TextUtils.equals(entry.getAnimation().getName(), "kuajiao")){
                    isClickAnimation = true;
                }
            }

            @Override
            public void interrupt(AnimationState.TrackEntry entry) {
                if(TextUtils.equals(entry.getAnimation().getName(), "kuajiao")){
                    isClickAnimation = false;
                }
            }

            @Override
            public void end(AnimationState.TrackEntry entry) {
                if(TextUtils.equals(entry.getAnimation().getName(), "kuajiao")){
                    isClickAnimation = false;
                }
            }

            @Override
            public void dispose(AnimationState.TrackEntry entry) {

            }

            @Override
            public void complete(AnimationState.TrackEntry entry) {
                if(TextUtils.equals(entry.getAnimation().getName(), "kuajiao")){
                    isClickAnimation = false;
                }
            }

            @Override
            public void event(AnimationState.TrackEntry entry, Event event) {

            }
        });
        Gdx.input.setInputProcessor(new InputAdapter() {
            final Vector3 point = new Vector3();

            public boolean touchDown (int screenX, int screenY, int pointer, int button) {
                camera.unproject(point.set(screenX, screenY, 0)); // Convert window to world coordinates.
                bounds.update(skeleton, true); // Update SkeletonBounds with current skeleton bounding box positions.
                if (bounds.aabbContainsPoint(point.x, point.y)) { // Check if inside AABB first. This check is fast.
                    BoundingBoxAttachment hit = bounds.containsPoint(point.x, point.y); // Check if inside a bounding box.
                    if (hit != null) {
                        System.out.println("hit: " + hit);
                        skeleton.findSlot("head").getColor().set(Color.RED); // Turn head red until touchUp.
                    }
                }
                return true;
            }

            public boolean touchUp (int screenX, int screenY, int pointer, int button) {
                if(!isClickAnimation){
                    state.setAnimation(0, "kuajiao", false); // Set animation on track 0 to jump.
                    state.addAnimation(0, "daiji", true, 0); // Queue run to play after jump.
                }
                return true;
            }

            public boolean keyDown (int keycode) {
                switch (keycode){
                    case 8:
                        state.setAnimation(0, "kuajiao", false); // Set animation on track 0 to jump.!
                        break;
                    case 9:
                        state.setAnimation(0, "diantou", false); // Set animation on track 0 to jump.
                        break;
                }
                state.addAnimation(0, "daiji", true, 0); // Queue run to play after jump.
                return true;
            }
        });

    }

    public void render() {
        state.update(Gdx.graphics.getDeltaTime()); // Update the animation time.

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glClearColor(0, 0, 0, 0);

        if (state.apply(skeleton)) // Poses skeleton using current animations. This sets the bones' local SRT.
        skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

        // Configure the camera, SpriteBatch, and SkeletonRendererDebug.
        camera.update();
        batch.getProjectionMatrix().set(camera.combined);
        debugRenderer.getShapeRenderer().setProjectionMatrix(camera.combined);

        batch.begin();
        renderer.draw(batch, skeleton); // Draw the skeleton images.
        batch.end();
        debugRenderer.draw(skeleton); // Draw debug lines.
    }

    public void resize(int width, int height) {
        camera.setToOrtho(false); // Update camera with new size.
    }

    public void dispose() {
        atlas.dispose();
    }

    public void setAnimate() {
        setAnimate("kuajiao");
        setAnimate("daiji"); // Run after the jump.
    }

    public void setAnimate(String animate) {
        state.addAnimation(0, animate, true, 0);
    }

    public void zoomBig() {
        camera.zoom = 0.5f;
    }

    public void zoomSmall() {
        camera.zoom = 1f;
    }
}
