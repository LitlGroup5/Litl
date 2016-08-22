package com.litlgroup.litl.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by andrj148 on 8/16/16.
 */
public class Task {
    public Date getDeadlineDate() {
        return deadlineDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

//    public Address getAddress() {
//        return address;
//    }

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

    public Task()
    {

    }

    private Date deadlineDate;
    private Date createdAt;
//    private Address address;
    private String description;
    private Type type;
    private State state;
    private String profileImageURL;
    private String workImageURL;
    private String workVideoURL;
    private ArrayList<String> categories;
    private Bookmark bookmark;
    private String title;
    private float price;
    private long id;
    private int favorPoints;


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

    public static Task fromTaskObjectMap(Map<String, Object> taskObjectMap)
    {
        try
        {
            Task task = new Task();
            task.price = Float.parseFloat(taskObjectMap.get("price").toString().replace("$",""));
            task.title = taskObjectMap.get("title").toString();
            task.description = taskObjectMap.get("description").toString();
            return task;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;

    }

}


