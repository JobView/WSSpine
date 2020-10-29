package com.ws.wsspine.model;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Skin;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.RegionAttachment;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SpineModelLocal extends AbstractSpineModelLocal {
    int animationIndex = 0;
    int skinIndex = 0;
    public SpineModelLocal(String atlasPath, String jsonPath, int parentW, int parentH) {
        super(atlasPath, jsonPath, parentW, parentH, E_SpineFitMode.FIT_HEIGHT);
    }

    ConcurrentLinkedQueue<String> animationQueue = new ConcurrentLinkedQueue<>();

    @Override
    protected void init() {
        animationState.setAnimation(0, getAnimations().get(0), false);
        setSkin(getSkins().get(getSkins().size - 1).getName());
//        setInputEvent();
        setAnimationListener();
    }

    private void setAnimationListener() {
        this.animationState.addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void start(AnimationState.TrackEntry entry) {
                Log.i("animationState:", "start:" + entry.getAnimation().getName());
            }

            @Override
            public void end(AnimationState.TrackEntry entry) {
                Log.i("animationState:", "end:" + entry.getAnimation().getName());
                if(!entry.getAnimation().getName().equals(getAnimations().get(0))){
//                    SpineModelLocal.this.animationState.setAnimation(0, getAnimations().get(0), true);
                }

            }

            @Override
            public void complete(AnimationState.TrackEntry entry) {
                Log.i("animationState:", "complete:" + entry.getAnimation().getName());
                loadNextAnimation(entry);
            }

        });
    }

    private void setInputEvent() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            public boolean touchDown (int screenX, int screenY, int pointer, int button) {
                setAnimation(getNextAnimationName());
                return true;
            }

            public boolean keyDown (int keycode) {
//                setSkin(getNextSkinName());
                return true;
            }
        });
    }

    public String getNextAnimationName(){
        animationIndex++;
        animationIndex = animationIndex % getAnimations().size();
        String name = getAnimations().get(animationIndex);
        return name;
    }

    public String getNextSkinName(){
        skinIndex++;
        skinIndex = skinIndex % getSkins().size;
        String name = getSkins().get(skinIndex).getName();
        return name;
    }

    /**
     * 通过部件的前缀换部分皮肤
     * @param partPix
     */
    public void changePartSlotByPix(String partPix, String skinName) {
        Skin skin = skeleton.getData().findSkin(skinName);
        if(skin == null) return;
        Slot slot;
        for (int i = 0; i < skeleton.getSlots().size; i++) {
            slot = skeleton.getSlots().get(i);
            if(slot.getData().getName().startsWith(partPix)){
                skeleton.setAttachment(slot.getData().getName(), null);
                if(slot.getData().getAttachmentName() != null){
                    Attachment attachment = skin.getAttachment(i, slot.getData().getAttachmentName());
                    if(attachment != null){
                        slot.setAttachment(attachment);
                    }
                }
            }
        }
    }

    public void changeSlotFromSkin(String slotName){
        Slot slot = skeleton.findSlot(slotName);
        Attachment attachment = getSkins().get(0).getAttachment(0, slotName);
        slot.setAttachment(attachment);
    }

    public void changeSlot(String slotName){
        Slot slot = skeleton.findSlot(slotName);
        RegionAttachment attachment = (RegionAttachment) slot.getAttachment();

        Texture texture1 = new Texture(Gdx.files.internal("assets/press.png"));
        attachment.getRegion().setTexture(texture1);
        attachment.updateOffset();
    }




    /**
     * 转化成换肤模式
     */
    public void setToChangeSkinStatus() {
        try{
            if(skeleton.getSkin() == null || !skeleton.getSkin().getName().equals(getSkins().get(1).getName())){
                setSkin(getSkins().get(1).getName());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void addAnimation(String animationName){
        if(getAnimations().isEmpty()){
            return;
        }
        if(!getAnimations().contains(animationName)){
            return;
        }

        if(getAnimations().get(0).equals(animationName)){ // 不能传入待机动作
            return;
        }
        animationQueue.offer(animationName);
    }

    private void loadNextAnimation(AnimationState.TrackEntry entry){
        String poll = animationQueue.poll();
        if(poll != null){
            this.animationState.setAnimation(0, poll, false);
        }else {
            if(!entry.getLoop()){
                this.animationState.setAnimation(0, getAnimations().get(0), true);
            }

        }
    }

}
