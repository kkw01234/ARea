package kr.co.area.hashtag.ar;

import android.location.Location;

/**
 * Created by ntdat on 1/16/17.
 */

public class ARPoint {
    Location location;
    String name;
    String id;

    public ARPoint(String id, String name, double lat, double lon) {
        this.id = id;
        this.name = name;
        location = new Location("ARPoint");
        location.setLatitude(lat);
        location.setLongitude(lon);
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return id;
    }
}
