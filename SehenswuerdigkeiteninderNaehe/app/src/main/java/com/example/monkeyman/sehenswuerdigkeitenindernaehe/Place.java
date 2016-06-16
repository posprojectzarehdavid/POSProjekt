package com.example.monkeyman.sehenswuerdigkeitenindernaehe;

import java.io.Serializable;

/**
 * Created by MonkeyMan on 09.06.2016.
 */
public class Place implements Serializable {
    double latitude;
    double longitude;
    int value; // 1 --> in Filter; 0 --> nicht in Filter
    String name;
    String iconLink;
    String [] pictureLinks;
    String address;
    String [] types;

    public Place(double latitude, double longitude, String name, String iconLink, String []pictureLinks, String address, String[] types) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.iconLink = iconLink;
        this.pictureLinks = pictureLinks;
        this.address = address;
        this.types = types;
        this.value = 0;
    }

    public String [] getPictureLinks() {
        return pictureLinks;
    }

    public void setPictureLink(String [] pictureLinks) {
        this.pictureLinks = pictureLinks;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public String getIconLink() {
        return iconLink;
    }

    public String getAddress() {
        return address;
    }

    public String[] getTypes() {
        return types;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name;
    }
}
