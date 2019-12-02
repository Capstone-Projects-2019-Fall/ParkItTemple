package com.example.parkittemple.database;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.List;

public class Street implements Serializable {

    private String streetName;
    private String piID;
    private List<GeoPoint> geoPoints;
    private Regulation regulation;
    private Calculation calculation;

    public String getStreetName() {
        return this.streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getPiID() {
        return this.piID;
    }

    public void setPiID(String piID) {
        this.piID = piID;
    }

    public List<GeoPoint> getGeoPoints() {
        return geoPoints;
    }

    public void setGeoPoints(List<GeoPoint> geoPoints) {
        this.geoPoints = geoPoints;
    }

    public Regulation getRegulation() {
        return regulation;
    }

    public void setRegulation(Regulation regulation) {
        this.regulation = regulation;
    }

    public Calculation getCalculation() {
        return calculation;
    }

    public void setCalculation(Calculation calculation) {
        this.calculation = calculation;
    }
}
