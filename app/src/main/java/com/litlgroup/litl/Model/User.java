package com.litlgroup.litl.model;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by andrj148 on 8/16/16.
 */

@Parcel
public class User {
    private String firstName;
    private String lastName;
    private Address address;
    private String biography;
    private String profileImageURL;
    private String thumbnailURL;
    private ArrayList<Review>vreviewsOfMe;
    private ArrayList<Review>vreviewsByMe;
    private String id;
    private ArrayList<String> categories;
    private ArrayList<Bookmark> bookmarks;
    private String email;
    private double money;
    private int favorPoints;
    private ArrayList<Connection> connections;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName()
    {
        return String.format("%s %s", firstName, lastName);
    }


    public Address getAddress() {
        return address;
    }

    public String getBiography() {
        return biography;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public ArrayList<Review> getVreviewsOfMe() {
        return vreviewsOfMe;
    }

    public ArrayList<Review> getVreviewsByMe() {
        return vreviewsByMe;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public ArrayList<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public String getEmail() {
        return email;
    }

    public double getMoney() {
        return money;
    }

    public int getFavorPoints() {
        return favorPoints;
    }

    public ArrayList<Connection> getConnections() {
        return connections;
    }


    public static User getFakeUser() {
        User fakeUser = new User();
        fakeUser.firstName = "Liz";
        fakeUser.lastName = "Lemon";
        fakeUser.biography = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua";
        fakeUser.profileImageURL = "http://yourblackworld.net/wp-content/uploads/2013/06/21/afro-puffs.jpg";

        return fakeUser;
    }
    public User() {
    }

    public static User fromUserObjectMap(Map<String, Object> userObjectMap)
    {
        try
        {
            User user = new User();

            user.firstName = userObjectMap.get("first_name").toString();
            user.lastName = userObjectMap.get("last_name").toString();
            user.profileImageURL = userObjectMap.get("profile_image_url").toString();

            return user;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

}
