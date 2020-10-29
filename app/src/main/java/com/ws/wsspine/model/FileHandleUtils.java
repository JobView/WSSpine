package com.ws.wsspine.model;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class FileHandleUtils {
    public static Pair<Float, Float> readSkeletonSize (FileHandle file) {
        Pair<Float, Float> result = new Pair<>(1f, 1f);
        if (file == null) throw new IllegalArgumentException("file cannot be null.");
        JsonValue root = new JsonReader().parse(file);

        JsonValue skeletonMap = root.get("skeleton");
        if (skeletonMap != null) {
            result = new Pair<>(skeletonMap.getFloat("width", 1), skeletonMap.getFloat("height", 1));
        }
        return result;
    }
}
