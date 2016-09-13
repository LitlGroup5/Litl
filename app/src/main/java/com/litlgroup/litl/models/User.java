package com.litlgroup.litl.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

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
    private List<String> rating;
    private List<String> skillSet = new ArrayList<String>();
    private Address address;
    private String biography;
    private List<String> connections = new ArrayList<String>();

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
    public List<String> getRating() {
        return rating;
    }

    /**
     *
     * @param rating
     * The rating
     */
    public void setRating(List<String> rating) {
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


    /**
     *
     * @return
     * The Connections
     */
    public List<String> getConnections() {
        return connections;
    }

    /**
     *
     * @param connections
     * The Connections
     */
    public void setConnections(List<String> connections) {
        this.connections = connections;
    }


    public User(
            List<String> bookmarks,
            String contactNo,
            Float earnings,
            String email,
            List<String> media,
            String name,
            String photo,
            List<String> rating,
            List<String> skillSet,
            Address address,
            String biography,
            List<String> connections)
    {
        this.contactNo = contactNo;
        this.bookmarks = bookmarks;
        this.earnings = earnings;
        this.email = email;
        this.media = media;
        this.name = name;
        this.photo = photo;
        this.rating = rating;
        this.skillSet = skillSet;
        this.address = address;
        this.biography = biography;
        this.connections = connections;
    }

    public float getAverageRating()
    {
        try
        {
            int total = 0;
            int numRatings = 0;
            for (int i =0; i<rating.size(); i++)
            {
                int ratingInt = Integer.parseInt(rating.get(i));
                if( ratingInt > 0) {
                    total += (ratingInt * (i + 1));
                    numRatings += ratingInt;
                }
            }

            if(numRatings == 0 || total == 0)
                return 0;

            float avgRating = (float)total / numRatings;
            return avgRating;
        }
        catch (Exception ex)
        {
            Timber.e("Error computing average rating");
        }
        return 0;
    }

    public Integer getNumberRatings()
    {
        try
        {
            int numRatings = 0;
            for (int i =0; i<rating.size(); i++)
            {
                if(Integer.parseInt(rating.get(i)) > 0)
                {
                    numRatings += Integer.parseInt(rating.get(i));
                }
            }
            return numRatings;
        }
        catch (Exception ex)
        {
            Timber.e("Error getting number of ratings");
        }
        return -1;
    }

}
