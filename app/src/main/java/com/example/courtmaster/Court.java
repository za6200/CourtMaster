package com.example.courtmaster;

import android.location.Location;

public class Court {
    private String courtID;
    private double latitude;  // instead of Location location
    private double longitude;
    private String courtName;
    private String facilities;

    public Court() {}

    public Court(String courtID, double latitude, double longitude, String courtName, String facilities) {
        this.courtID = courtID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.courtName = courtName;
        this.facilities = facilities;
    }

    public String getCourtID() {
        return courtID;
    }

    public void setCourtID(String courtID) {
        this.courtID = courtID;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude()
    {
        return this.longitude;
    }
    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }
}
