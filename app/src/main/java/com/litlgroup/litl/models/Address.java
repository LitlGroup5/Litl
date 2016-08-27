package com.litlgroup.litl.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by monusurana on 8/26/16.
 */
@IgnoreExtraProperties
public class Address {

    private String apt;
    private String city;
    private Integer houseNo;
    private String state;
    private String streetAddress;
    private Integer zip;

    public Address() {
    }

    /**
     *
     * @return
     * The apt
     */
    public String getApt() {
        return apt;
    }

    /**
     *
     * @param apt
     * The apt
     */
    public void setApt(String apt) {
        this.apt = apt;
    }

    /**
     *
     * @return
     * The city
     */
    public String getCity() {
        return city;
    }

    /**
     *
     * @param city
     * The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     *
     * @return
     * The houseNo
     */
    public Integer getHouseNo() {
        return houseNo;
    }

    /**
     *
     * @param houseNo
     * The houseNo
     */
    public void setHouseNo(Integer houseNo) {
        this.houseNo = houseNo;
    }

    /**
     *
     * @return
     * The state
     */
    public String getState() {
        return state;
    }

    /**
     *
     * @param state
     * The state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     *
     * @return
     * The streetAddress
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     *
     * @param streetAddress
     * The streetAddress
     */
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    /**
     *
     * @return
     * The zip
     */
    public Integer getZip() {
        return zip;
    }

    /**
     *
     * @param zip
     * The zip
     */
    public void setZip(Integer zip) {
        this.zip = zip;
    }
}
