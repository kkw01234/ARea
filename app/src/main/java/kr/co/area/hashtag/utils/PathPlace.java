package kr.co.area.hashtag.utils;

import java.io.Serializable;

public class PathPlace implements Serializable {
    public String id;
    public String name;
    public double latitude;
    public double longitude;

    public PathPlace(String id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}