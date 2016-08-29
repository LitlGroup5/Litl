package com.litlgroup.litl.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by monusurana on 8/26/16.
 */
@IgnoreExtraProperties
public class User {

    private List<String> bookmarks = new ArrayList<String>();
    private String contactNo;
    private Float earnings;
    private String email;
    private List<String> media = new ArrayList<String>();
    private String name;
    private String photo;
    private Float rating;
    private List<String> skillSet = new ArrayList<String>();
    private Address address;
    private String biography;

    public User() {
    }

    /**
     *
     * @return
     * The bookmarks
     */
    public List<String> getBookmarks() {
        return bookmarks;
    }

    /**
     *
     * @param bookmarks
     * The bookmarks
     */
    public void setBookmarks(List<String> bookmarks) {
        this.bookmarks = bookmarks;
    }

    /**
     *
     * @return
     * The contactNo
     */
    public String getContactNo() {
        return contactNo;
    }

    /**
     *
     * @param contactNo
     * The contactNo
     */
    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    /**
     *
     * @return
     * The earnings
     */
    public Float getEarnings() {
        return earnings;
    }

    /**
     *
     * @param earnings
     * The earnings
     */
    public void setEarnings(Float earnings) {
        this.earnings = earnings;
    }

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     * The media
     */
    public List<String> getMedia() {
        return media;
    }

    /**
     *
     * @param media
     * The media
     */
    public void setMedia(List<String> media) {
        this.media = media;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The photo
     */
    public String getPhoto() {
        return photo;
    }

    /**
     *
     * @param photo
     * The photo
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     *
     * @return
     * The rating
     */
    public Float getRating() {
        return rating;
    }

    /**
     *
     * @param rating
     * The rating
     */
    public void setRating(Float rating) {
        this.rating = rating;
    }

    /**
     *
     * @return
     * The skillSet
     */
    public List<String> getSkillSet() {
        return skillSet;
    }

    /**
     *
     * @param skillSet
     * The skillSet
     */
    public void setSkillSet(List<String> skillSet) {
        this.skillSet = skillSet;
    }


    /**
     * @return The address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * @param address The address
     */
    public void setAddress(Address address) {
        this.address = address;
    }


    /**
     * @return The biography
     */
    public String getbiography() {
        return biography;
    }

    /**
     * @param biography
     * The biography
     */
    public void setBiography(String biography) {
        this.biography = biography;
    }


}
