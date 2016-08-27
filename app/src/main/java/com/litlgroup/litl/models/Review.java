package com.litlgroup.litl.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by monusurana on 8/25/16.
 */
@IgnoreExtraProperties
public class Review {

    private String comment;
    private String createdOn;
    private Float rating;
    private String reviewBy;
    private String reviewOf;
    private String taskId;

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
     * The createdOn
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
     * The reviewBy
     */
    public String getReviewBy() {
        return reviewBy;
    }

    /**
     *
     * @param reviewBy
     * The reviewBy
     */
    public void setReviewBy(String reviewBy) {
        this.reviewBy = reviewBy;
    }

    /**
     *
     * @return
     * The reviewOf
     */
    public String getReviewOf() {
        return reviewOf;
    }

    /**
     *
     * @param reviewOf
     * The reviewOf
     */
    public void setReviewOf(String reviewOf) {
        this.reviewOf = reviewOf;
    }

    /**
     *
     * @return
     * The taskid
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     *
     * @param taskId
     * The taskId
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

}
