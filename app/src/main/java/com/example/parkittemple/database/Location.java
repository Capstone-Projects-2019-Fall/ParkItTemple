package com.example.parkittemple.database;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class Location {

    private List<GeoPoint> geoPoints;

    public List<GeoPoint> getGeoPoints() {
        return geoPoints;
    }

    public void setGeoPoints(List<GeoPoint> geoPoints) {
        this.geoPoints = geoPoints;
    }
}
