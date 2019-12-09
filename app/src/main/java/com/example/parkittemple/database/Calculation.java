package com.example.parkittemple.database;

import java.io.Serializable;

public class Calculation implements Serializable {

    private long availableSpots;
    private long totalSpots;
    private String probability;
    private long takenSpots;

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

    public long getTakenSpots() {
        return takenSpots;
    }

    public void setTakenSpots(long takenSpots) {
        this.takenSpots = takenSpots;
    }

    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
    }
}