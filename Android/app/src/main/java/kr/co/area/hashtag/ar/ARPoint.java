package kr.co.area.hashtag.ar;

import android.location.Location;

/**
 * Created by ntdat on 1/16/17.
 */

public class ARPoint {
    Location location;
    String name;

    public ARPoint(String name, double lat, double lon, double altitude) {
        this.name = name;
        location = new Location("ARPoint");
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setAltitude(altitude);
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public boolean equals(Object o) {
        ARPoint a = (ARPoint) o;
        return (name == a.name) && (location.getLatitude() == a.location.getLatitude())
                && (location.getLongitude() == a.location.getLongitude()) &&
                (location.getAltitude() == a.location.getAltitude());
    }
}
