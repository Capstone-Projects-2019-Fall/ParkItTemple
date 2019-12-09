package com.example.parkittemple.database;

import java.util.ArrayList;

public class ParkingLot {

    private String name, daysOpen, hours, costs, address;
    private ArrayList<com.google.android.gms.maps.model.LatLng> points;

    public ParkingLot(String name, String daysOpen, String hours, String costs, String address, ArrayList<com.google.android.gms.maps.model.LatLng> points) {
        this.name = name;
        this.daysOpen = daysOpen;
        this.hours = hours;
        this.costs = costs;
        this.address = address;
        this.points = points;
    }

    public ArrayList<com.google.android.gms.maps.model.LatLng> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<com.google.android.gms.maps.model.LatLng> points) {
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDaysOpen() {
        return daysOpen;
    }

    public void setDaysOpen(String daysOpen) {
        this.daysOpen = daysOpen;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getCosts() {
        return costs;
    }

    public void setCosts(String costs) {
        this.costs = costs;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
