package com.fitness.myapplication;
import com.google.android.gms.maps.model.Marker;

public class Gym {
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private float distance;
    private Marker marker;

    public Gym(String name, String address, double latitude, double longitude,float distance) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
    public float getDistance() {
        return distance;
    }
    public void setDistance(float distance) {
        this.distance=distance;
    }
}