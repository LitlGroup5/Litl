package com.litlgroup.litl.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.litlgroup.litl.utils.Constants;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by monusurana on 8/26/16.
 */
@Parcel
@IgnoreExtraProperties
public class Task {

    private String acceptedOfferId;
    private Address address;
    private Integer bidBy;
    private Map<String, Boolean> bookmarks = new HashMap<>();
    private List<String> categories = new ArrayList<String>();
    private String deadlineDate;
    private String description;
    private List<String> media = new ArrayList<String>();
    private String price;
    private String status;
    private String title;
    private UserSummary user;
    private Integer viewedBy;
    private String id;
    private Type type;

    public Task() {
    }

    /**
     * @return The acceptedOfferId
     */
    public String getAcceptedOfferId() {
        return acceptedOfferId;
    }

    /**
     * @param acceptedOfferId The acceptedOfferId
     */
    public void setAcceptedOfferId(String acceptedOfferId) {
        this.acceptedOfferId = acceptedOfferId;
    }

    /**
     * @return The address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * @param address The address
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * @return The bidBy
     */
    public Integer getBidBy() {
        return bidBy;
    }

    /**
     * @param bidBy The bidBy
     */
    public void setBidBy(Integer bidBy) {
        this.bidBy = bidBy;
    }

    public void setType(Type type) {
        this.type = type;
    }

    /**
     * @return The categories
     */
    public List<String> getCategories() {
        return categories;
    }

    /**
     * @param categories The categories
     */
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    /**
     * @return The deadlineDate
     */
    public String getDeadlineDate() {
        return deadlineDate;
    }

    /**
     * @param deadlineDate The deadlineDate
     */
    public void setDeadlineDate(String deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The media
     */
    public List<String> getMedia() {
        return media;
    }

    /**
     * @param media The media
     */
    public void setMedia(List<String> media) {
        this.media = media;
    }

    /**
     * @return The price
     */
    public String getPrice() {
        return price;
    }

    /**
     * @param price The price
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The user
     */
    public UserSummary getUser() {
        return user;
    }

    /**
     * @param user The user
     */
    public void setUser(UserSummary user) {
        this.user = user;
    }

    /**
     * @return The viewedBy
     */
    public Integer getViewedBy() {
        return viewedBy;
    }

    /**
     * @param viewedBy The viewedBy
     */
    public void setViewedBy(Integer viewedBy) {
        this.viewedBy = viewedBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public Map<String, Boolean> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(Map<String, Boolean> bookmarks) {
        this.bookmarks = bookmarks;
    }

    public static boolean isBookmarked(Task task) {
        Map<String, Boolean> bookmarks = task.getBookmarks();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            return bookmarks.containsKey(uid);
        }

        return false;
    }

    public static void updateBookmark(Task task, boolean add) {
        Map<String, Boolean> bookmarks = task.getBookmarks();

        Map<String, Object> temp = new HashMap<>();
        if (add) {
            bookmarks.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), true);
            temp.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), true);
            FirebaseDatabase.getInstance().getReference().child(Constants.TABLE_TASKS).child(task.getId()).child("bookmarks").updateChildren(temp);
        } else {
            bookmarks.remove(FirebaseAuth.getInstance().getCurrentUser().getUid());
            FirebaseDatabase.getInstance().getReference().child(Constants.TABLE_TASKS).child(task.getId()).child("bookmarks").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
        }
    }

    public enum Type {
        PROPOSAL,
        OFFER,
        CLOSED
    }

    public enum State {
        IN_BIDDING_PROCESS,
        SUCCESSFULLY_ACCEPTED,
        NOT_ACCEPTED,
        EXPIRED
    }

    public static String getTimestampMillis(String dueDate, String dueTime) {
        try {
            if (dueDate.equals("") | !dueDate.contains("/"))
                return "";

            String[] splitDate = dueDate.split("/");
            int month = Integer.parseInt(splitDate[0]);
            int day = Integer.parseInt(splitDate[1]);
            int year = Integer.parseInt(splitDate[2]);

            Calendar calendar = Calendar.getInstance();
            if (dueTime.equals("") | !dueTime.contains(":")) {

                calendar.set(year, month, day);
                return String.valueOf(calendar.getTimeInMillis());
            }
            String[] splitTime = dueTime.split(":");
            int hour = Integer.parseInt(splitTime[0]);
            if (dueTime.contains("pm"))
                hour += 12;
            String[] secondSplitString = (splitTime[1].split(" "));
            int minute = Integer.parseInt(secondSplitString[0]);

            calendar.set(year, month, day, hour, minute);
            String timeStampMS = String.valueOf(calendar.getTimeInMillis());

            return timeStampMS;
        } catch (Exception ex) {
            ex.printStackTrace();
            Timber.e(ex.toString());
        }
        return null;
    }

    public Task(Address address, List<String> categories, String deadline_date, String description, List<String> media, String price, String title, String status) {
        this.address = address;
        this.categories = categories;
        this.deadlineDate = deadline_date;
        this.description = description;
        this.media = media;
        this.price = price;
        this.title = title;
        this.status = status;
        this.viewedBy = 0;
        this.bidBy = 0;
        this.acceptedOfferId = "-1";
    }


    public Task(Address address, List<String> categories, String deadline_date, String description,
                List<String> media, String price, String title, String status, int viewedBy, int bidBy) {
        this.address = address;
        this.categories = categories;
        this.deadlineDate = deadline_date;
        this.description = description;
        this.media = media;
        this.price = price;
        this.title = title;
        this.status = status;
        this.viewedBy = viewedBy;
        this.bidBy = bidBy;
        this.acceptedOfferId = "-1";
    }

    public Map<String, Object> toMap() {

        try {
            HashMap<String, Object> result = new HashMap<>();
            result.put("address", address);
            result.put("categories", categories);
            result.put("media", media);
            result.put("price", price);
            result.put("title", title);
            result.put("status", status);
            result.put("viewedBy", viewedBy);
            result.put("bidBy", bidBy);
            result.put("acceptedOfferId", acceptedOfferId);

            return result;

        } catch (Exception ex) {
            Timber.e("Error creating map", ex);
        }
        return null;
    }

    public static ArrayList<Task> getSortedTasks(ArrayList<Task> tasks, String sortByCategory) {
        ArrayList<Task> taskList = new ArrayList<>();

        // Only getting the first item for now, as there would not be multiple categories
        for (final Task sortedTask : tasks) {
            if (sortedTask.getCategories().get(0).equalsIgnoreCase(sortByCategory)) {
                taskList.add(sortedTask);
            }
        }

        return taskList;
    }
}