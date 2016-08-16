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


    public enum Type {
        PROPOSAL, OFFER
    }
    public enum State {
        inBiddingProcess, successfullyAccepted, notAccepted, EXPIRED
    }

}


