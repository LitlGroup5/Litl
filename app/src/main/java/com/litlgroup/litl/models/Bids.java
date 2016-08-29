package com.litlgroup.litl.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by monusurana on 8/26/16.
 */
@IgnoreExtraProperties
public class Bids {

    private String createdOn;
    private Float price;
    private String task;
    private UserSummary user;

    public Bids() {
    }

    /**
     *
     * @return
     * The createdAt
     */
    public String getCreatedOn() {
        return createdOn;
    }

    /**
     *
     * @param createdOn
     * The createdOn
     */
    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    /**
     *
     * @return
     * The price
     */
    public Float getPrice() {
        return price;
    }

    /**
     *
     * @param price
     * The price
     */
    public void setPrice(Float price) {
        this.price = price;
    }

    /**
     *
     * @return
     * The task
     */
    public String getTask() {
        return task;
    }

    /**
     *
     * @param task
     * The task
     */
    public void setTask(String task) {
        this.task = task;
    }

    /**
     *
     * @return
     * The user
     */
    public UserSummary getUser() {
        return user;
    }

    /**
     *
     * @param user
     * The user
     */
    public void setUser(UserSummary user) {
        this.user = user;
    }

}
