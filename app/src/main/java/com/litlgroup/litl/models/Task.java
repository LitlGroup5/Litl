package com.litlgroup.litl.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by monusurana on 8/26/16.
 */
@IgnoreExtraProperties
public class Task {

    private String acceptedOfferId;
    private Address address;
    private Integer bidBy;
    private List<String> categories = new ArrayList<String>();
    private String deadlineDate;
    private String description;
    private List<String> media = new ArrayList<String>();
    private String price;
    private String status;
    private String title;
    private UserSummary user;
    private Integer viewedBy;

    public Task() {
    }

    /**
     *
     * @return
     * The acceptedOfferId
     */
    public String getAcceptedOfferId() {
        return acceptedOfferId;
    }

    /**
     *
     * @param acceptedOfferId
     * The acceptedOfferId
     */
    public void setAcceptedOfferId(String acceptedOfferId) {
        this.acceptedOfferId = acceptedOfferId;
    }

    /**
     *
     * @return
     * The address
     */
    public Address getAddress() {
        return address;
    }

    /**
     *
     * @param address
     * The address
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     *
     * @return
     * The bidBy
     */
    public Integer getBidBy() {
        return bidBy;
    }

    /**
     *
     * @param bidBy
     * The bidBy
     */
    public void setBidBy(Integer bidBy) {
        this.bidBy = bidBy;
    }

    /**
     *
     * @return
     * The categories
     */
    public List<String> getCategories() {
        return categories;
    }

    /**
     *
     * @param categories
     * The categories
     */
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    /**
     *
     * @return
     * The deadlineDate
     */
    public String getDeadlineDate() {
        return deadlineDate;
    }

    /**
     *
     * @param deadlineDate
     * The deadlineDate
     */
    public void setDeadlineDate(String deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
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
     * The price
     */
    public String getPrice() {
        return price;
    }

    /**
     *
     * @param price
     * The price
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
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

    /**
     *
     * @return
     * The viewedBy
     */
    public Integer getViewedBy() {
        return viewedBy;
    }

    /**
     *
     * @param viewedBy
     * The viewedBy
     */
    public void setViewedBy(Integer viewedBy) {
        this.viewedBy = viewedBy;
    }

}