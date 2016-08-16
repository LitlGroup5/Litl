package com.litlgroup.litl.Model;

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

}


