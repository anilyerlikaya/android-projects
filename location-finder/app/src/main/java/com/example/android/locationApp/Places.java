package com.example.android.locationApp;

import android.graphics.Bitmap;
import android.graphics.drawable.Icon;

import java.net.URL;

public class Places {

    private double latitude;
    private double longitude;
    private String locationName;
    private Bitmap locationIcon;
    // Todo: get information for locations
    private String locationInfo;
    private String locationId;

    public Places(double latitude, double longitude, String locationName){
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
    }

    public Places(double latitude, double longitude, String locationName, Bitmap locationIcon, String locationId ){
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.locationIcon = locationIcon;
        this.locationId = locationId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public Bitmap getLocationIcon() { return locationIcon; }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setLocationIcon(Bitmap locationIcon) { this.locationIcon = locationIcon; }

    public String getLocationId() {
        return locationId;
    }

    public String getLocationInfo() {
        return locationInfo;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public void setLocationInfo(String locationInfo) {
        this.locationInfo = locationInfo;
    }

    @Override
    public String toString() {
        return "Places{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", locationName='" + locationName + '\'' +
                ", locationIcon=" + locationIcon +
                ", locationInfo='" + locationInfo + '\'' +
                ", locationId='" + locationId + '\'' +
                '}';
    }
}
