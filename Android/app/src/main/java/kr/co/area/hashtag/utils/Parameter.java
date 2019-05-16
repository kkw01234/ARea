package kr.co.area.hashtag.utils;

import android.graphics.Bitmap;

public class Parameter {
    private String key, value;

    public Parameter(String key, String value) {
        this.key = key;
        this.value = value;
    }


    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
