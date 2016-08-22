package com.litlgroup.litl.model;

import android.location.Location;

/**
 * Created by andrj148 on 8/16/16.
 */
public class Address {
    private int houseNumber;
    private String street;
    private String city;
    private String stateAbbreviation;
    private int zipcode;
    private Location location;

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStateAbbreviation(String stateAbbreviation) {
        this.stateAbbreviation = stateAbbreviation;
    }

    public void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getFormattedAddress() {
        return String.valueOf(houseNumber) + " " + street + ", " + city + ", " + stateAbbreviation + ", " + String.valueOf(zipcode);
    }

}
