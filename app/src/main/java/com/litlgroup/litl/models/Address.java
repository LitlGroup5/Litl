package com.litlgroup.litl.models;

import com.google.firebase.database.IgnoreExtraProperties;

import timber.log.Timber;

/**
 * Created by monusurana on 8/26/16.
 */
@IgnoreExtraProperties
public class Address {

    private String apt;
    private String city;
    private Integer house_no;
    private String state;
    private String street_address;
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
     * The house_no
     */
    public Integer getHouse_no() {
        return house_no;
    }

    /**
     *
     * @param house_no
     * The house_no
     */
    public void setHouse_no(Integer house_no) {
        this.house_no = house_no;
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
     * The street_address
     */
    public String getStreet_address() {
        return street_address;
    }

    /**
     *
     * @param street_address
     * The street_address
     */
    public void setStreet_address(String street_address) {
        this.street_address = street_address;
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


    public static String getDisplayString(Address address)
    {
        try
        {
            String apt = "";
            if(address.getApt()!=null && !address.getApt().isEmpty())
                apt = String.format(" Apt %s ", address.getApt());

            if(address.getHouse_no() == null
                    || address.getStreet_address() == null
                    || address.getCity()== null
                    || address.getState()== null
                    || address.getZip()== null
                    )
                return "";

            String displayString =
                    String.format("%s %s %s%s %s %s",
                            address.getHouse_no(),
                            address.getStreet_address(),
                            apt,
                            address.getCity(),
                            address.getState(),
                            address.getZip()
                    );

            return displayString;
        }
        catch (Exception ex)
        {
            Timber.e("Error generating display string");
        }
        return "";
    }
}
