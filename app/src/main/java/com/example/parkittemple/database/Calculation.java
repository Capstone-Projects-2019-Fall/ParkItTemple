package com.example.parkittemple.database;

import java.io.Serializable;

public class Calculation implements Serializable {

    private long availableSpots;
    private long totalSpots;
    private String probability;

    public long getAvailableSpots() {
        return availableSpots;
    }

    public void setAvailableSpots(long spotsAvailable) {
        this.availableSpots = spotsAvailable;
    }

    public long getTotalSpots() {
        return totalSpots;
    }

    public void setTotalSpots(long totalSpots) {
        this.totalSpots = totalSpots;
    }

    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
    }
}