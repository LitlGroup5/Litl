package com.litlgroup.litl.model;

import java.util.ArrayList;

/**
 * Created by andrj148 on 8/16/16.
 */
public class User {
    private String firstName;
    private String lastName;
    private Address address;
    private String biography;
    private String profileImageURL;
    private String thumbnailURL;
    private ArrayList<Review>vreviewsOfMe;
    private ArrayList<Review>vreviewsByMe;
    private long id;
    private ArrayList<String> categories;
    private ArrayList<Bookmark> bookmarks;
    private String email;
    private float money;
    private int favorPoints;
    private ArrayList<Connection> connections;
}
