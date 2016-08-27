package com.litlgroup.litl.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by monusurana on 8/26/16.
 */
@IgnoreExtraProperties
public class Bids {

    private String created_at;
    private Float price;
    private String task;
    private String user;

    public Bids() {
    }

    /**
     *
     * @return
     * The createdAt
     */
    public String getCreatedAt() {
        return created_at;
    }

    /**
     *
     * @param created_at
     * The created_at
     */
    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
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
    public String getUser() {
        return user;
    }

    /**
     *
     * @param user
     * The user
     */
    public void setUser(String user) {
        this.user = user;
    }

}
