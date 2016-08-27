package com.litlgroup.litl.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by monusurana on 8/26/16.
 */
@IgnoreExtraProperties
public class Task {

    private String accepted_offer_id;
    private Address address;
    private Integer bid_by;
    private List<String> categories = new ArrayList<String>();
    private String deadline_date;
    private String description;
    private List<String> media = new ArrayList<String>();
    private String price;
    private String status;
    private String title;
    private UserSummary user;
    private Integer viewed_by;

    public Task() {
    }

    /**
     *
     * @return
     * The accepted_offer_id
     */
    public String getAcceptedOfferId() {
        return accepted_offer_id;
    }

    /**
     *
     * @param accepted_offer_id
     * The accepted_offer_id
     */
    public void setAcceptedOfferId(String accepted_offer_id) {
        this.accepted_offer_id = accepted_offer_id;
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
        return bid_by;
    }

    /**
     *
     * @param bidBy
     * The bid_by
     */
    public void setBidBy(Integer bidBy) {
        this.bid_by = bidBy;
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
        return deadline_date;
    }

    /**
     *
     * @param deadline_date
     * The deadline_date
     */
    public void setDeadlineDate(String deadline_date) {
        this.deadline_date = deadline_date;
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
     * The viewed_by
     */
    public Integer getViewedBy() {
        return viewed_by;
    }

    /**
     *
     * @param viewed_by
     * The viewed_by
     */
    public void setViewedBy(Integer viewed_by) {
        this.viewed_by = viewed_by;
    }

}