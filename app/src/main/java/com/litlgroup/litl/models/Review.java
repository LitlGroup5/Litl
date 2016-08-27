package com.litlgroup.litl.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by monusurana on 8/25/16.
 */
@IgnoreExtraProperties
public class Review {

    private String comment;
    private String created_on;
    private Float rating;
    private String reviewby;
    private String reviewof;
    private String taskid;

    public Review() {
    }

    /**
     *
     * @return
     * The comment
     */
    public String getComment() {
        return comment;
    }

    /**
     *
     * @param comment
     * The comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     *
     * @return
     * The created_on
     */
    public String getCreated_on() {
        return created_on;
    }

    /**
     *
     * @param created_on
     * The created_on
     */
    public void setCreated_on(String created_on) {
        this.created_on = created_on;
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
     * The reviewby
     */
    public String getReviewby() {
        return reviewby;
    }

    /**
     *
     * @param reviewby
     * The reviewby
     */
    public void setReviewby(String reviewby) {
        this.reviewby = reviewby;
    }

    /**
     *
     * @return
     * The reviewof
     */
    public String getReviewof() {
        return reviewof;
    }

    /**
     *
     * @param reviewof
     * The reviewof
     */
    public void setReviewof(String reviewof) {
        this.reviewof = reviewof;
    }

    /**
     *
     * @return
     * The taskid
     */
    public String getTaskid() {
        return taskid;
    }

    /**
     *
     * @param taskid
     * The taskid
     */
    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

}
