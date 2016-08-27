package com.litlgroup.litl.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import timber.log.Timber;

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

    public static String getTimestampMillis(String dueDate, String dueTime)
    {
        try
        {
            if(dueDate.equals("") | !dueDate.contains("/"))
                return "";

            String[] splitDate = dueDate.split("/");
            int month = Integer.parseInt(splitDate[0]);
            int day = Integer.parseInt(splitDate[1]);
            int year = Integer.parseInt(splitDate[2]);

            Calendar calendar = Calendar.getInstance();
            if(dueTime.equals("") | !dueTime.contains(":")) {

                calendar.set(year, month, day);
                return String.valueOf(calendar.getTimeInMillis());
            }
            String[] splitTime = dueTime.split(":");
            int hour = Integer.parseInt(splitTime[0]);
            String[] secondSplitString = (splitTime[1].split(" "));
            int minute = Integer.parseInt(secondSplitString[0]);

            calendar.set(year, month, day, hour, minute);
            String timeStampMS = String.valueOf(calendar.getTimeInMillis());

            return timeStampMS;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Timber.e(ex.toString());
        }
        return null;
    }

    public Task(Address address, List<String> categories, String deadline_date, String description, List<String> media, String price, String title, String status) {
        this.address = address;
        this.categories = categories;
        this.deadline_date = deadline_date;
        this.description = description;
        this.media = media;
        this.price = price;
        this.title = title;
        this.status = status;
        this.viewed_by = 0;
        this.bid_by = 0;
        this.accepted_offer_id = "-1";
    }
}