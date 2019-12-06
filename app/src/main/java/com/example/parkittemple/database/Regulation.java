package com.example.parkittemple.database;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Regulation implements Serializable {

    private String description;
    private String note;

    private boolean free;

    private Timestamp start;
    private Timestamp end;
    private String maxHours;

   // private float start;
    //private float end;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public String getMaxHours() {
        return maxHours;
    }

    public void setMaxHours(String max) {
        this.maxHours = max;
    }
}
