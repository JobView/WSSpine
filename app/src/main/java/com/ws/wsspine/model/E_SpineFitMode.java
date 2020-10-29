package com.ws.wsspine.model;

public enum E_SpineFitMode {
    /**
     * 适配高度
     */
    FIT_HEIGHT {
        @Override
        float getScale(float viewW, float viewH, float jsonW, float jsonH){
            return viewH / jsonH;
        }
        @Override
        float[] getPosition(float viewW, float viewH, float jsonW, float jsonH){
            float[] position = new float[]{viewW / 2, 0};
            return position;
        }
    },

    /**
     * 居中适配
     */
    FIT_INSIDE {
        @Override
        float getScale(float viewW, float viewH, float jsonW, float jsonH) {
            return Math.min(viewW / jsonW, viewH / jsonH);
        }

        @Override
        float[] getPosition(float viewW, float viewH, float jsonW, float jsonH) {
            float[] position = new float[]{viewW/ 2, (viewW / jsonW) > (viewH / jsonH)? 0 :
                    (viewH - jsonH * getScale(viewW, viewH, jsonW, jsonH))  / 2};
            return position;
        }
    };

    /**
     * 获取缩放比
     * @param viewW view宽度
     * @param viewH view高度
     * @param jsonW 配置宽度
     * @param jsonH 配置高低
     * @return 比例
     */
    abstract float getScale(float viewW, float viewH, float jsonW, float jsonH);

    /**
     * 获取位置
     * @param viewW view宽度
     * @param viewH view高度
     * @param jsonW 配置宽度
     * @param jsonH 配置高低
     * @return 位置
     */
    abstract float[] getPosition(float viewW, float viewH, float jsonW, float jsonH);
}
