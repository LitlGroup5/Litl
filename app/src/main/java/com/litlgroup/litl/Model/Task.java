package com.litlgroup.litl.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by andrj148 on 8/16/16.
 */
public class Task {
    private Date deadlineDate;
    private Date createdAt;
    private Address address;
    private String description;
    private Type type;
    private State state;
    private String profileImageURL;
    private String workImageURL;
    private String workVideoURL;
    private ArrayList<String> categories;
    private String title;
    private float price;
    private long id;
    private int favorPoints;
    private Bookmark bookmark;
    private User user;

    public Date getDeadlineDate() {
        return deadlineDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Address getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public Type getType() {
        return type;
    }

    public State getState() {
        return state;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public String getWorkImageURL() {
        return workImageURL;
    }

    public String getWorkVideoURL() {
        return workVideoURL;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public Bookmark getBookmark() {
        return bookmark;
    }

    public String getTitle() {
        return title;
    }

    public float getPrice() {
        return price;
    }

    public long getId() {
        return id;
    }

    public int getFavorPoints() {
        return favorPoints;
    }

    public User getUser() {
        return user;
    }


    public enum Type {
        PROPOSAL,
        OFFER;
    }
    public enum State {
        inBiddingProcess,
        successfullyAccepted,
        notAccepted,
        EXPIRED;
    }

}


