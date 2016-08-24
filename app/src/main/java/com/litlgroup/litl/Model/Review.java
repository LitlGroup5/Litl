package com.litlgroup.litl.model;

import org.parceler.Parcel;

import java.util.Date;

/**
 * Created by andrj148 on 8/16/16.
 */

@Parcel
public class Review {
    private User reviewedBy;
    private User reviewOf;
    private String id;
    private Date createdAt;
}
