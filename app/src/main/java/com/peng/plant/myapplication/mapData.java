package com.peng.plant.myapplication;

import android.graphics.Bitmap;

import java.io.Serializable;

class mapData implements Serializable {

    public String name;
    public String img_path;
    public double latitude;
    public double longitude;
    public double distance;

    public Bitmap getBitmap_Marker() {
        return bitmap_Marker;
    }

    public void setBitmap_Marker(Bitmap bitmap_Marker) {
        this.bitmap_Marker = bitmap_Marker;
    }

    Bitmap bitmap_Marker;

    public mapData() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }


}
