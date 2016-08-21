package com.litlgroup.litl.model;

import java.util.Date;

/**
 * Created by andrj148 on 8/16/16.
 */
public class Bookmark {
    private Task bookmarkedTask;
    private Date createdAt;
    private Boolean isSyncedWithRemoteStorage;
    private Boolean isBookmarked;
    private User user;
}
