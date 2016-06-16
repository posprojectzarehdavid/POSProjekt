package com.example.monkeyman.sehenswuerdigkeitenindernaehe;

import java.io.Serializable;

/**
 * Created by MonkeyMan on 09.06.2016.
 */
public class Place implements Serializable {
    double latitude;
    double longitude;
    String name;
    String iconLink;
    String address;
    String [] types;

    public Place(double latitude, double longitude, String name, String iconLink, String address, String[] types) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.iconLink = iconLink;
        this.address = address;
        this.types = types;
    }

    @Override
    public String toString() {
        return name;
    }
}
