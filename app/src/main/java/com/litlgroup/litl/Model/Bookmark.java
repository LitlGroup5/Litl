package com.litlgroup.litl.model;

import org.parceler.Parcel;

import java.util.Date;

/**
 * Created by andrj148 on 8/16/16.
 */
@Parcel
public class Bookmark {
    private Task bookmarkedTask;
    private Date createdAt;
    private Boolean isSyncedWithRemoteStorage;
    private Boolean isBookmarked;
    private User user;


    public User getUser() {
        return user;
    }

    public Task getBookmarkedTask() {
        return bookmarkedTask;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Boolean getSyncedWithRemoteStorage() {
        return isSyncedWithRemoteStorage;
    }

    public Boolean getBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(Boolean bookmarked) {
        isBookmarked = bookmarked;
    }
}
